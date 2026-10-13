import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

// Credenciais do app OAuth do Feedly (fora do controle de versão):
// adicione feedly.clientId / feedly.clientSecret em local.properties
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

android {
    namespace = "com.greader.neo"
    compileSdk = 37
    compileSdkMinor = 0

    defaultConfig {
        applicationId = "com.greader.neo"
        minSdk = 33 // Android 13+: cobre o fluxo de permissão de notificação e amplia a base de aparelhos
        targetSdk = 36
        versionCode = 2
        versionName = "0.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Credenciais OAuth do Feedly.
        // Padrão: credenciais legadas do gReader (extraídas do APK original na análise) para
        // funcionar imediatamente. Recomendado registrar um app próprio no Feedly e sobrescrever
        // via local.properties (feedly.clientId / feedly.clientSecret).
        buildConfigField(
            "String",
            "FEEDLY_CLIENT_ID",
            "\"${localProperties.getProperty("feedly.clientId") ?: "greader"}\"",
        )
        buildConfigField(
            "String",
            "FEEDLY_CLIENT_SECRET",
            "\"${localProperties.getProperty("feedly.clientSecret") ?: "1DFHBT3YNACQADNK9IZ0IOXG"}\"",
        )
    }

    signingConfigs {
        // Assinatura da release: keystore local fora do repositório (~/.local/share/greader-neo/),
        // com caminho e senhas em local.properties (não versionado). Sem essas propriedades,
        // o build segue gerando APK sem assinatura.
        val storePath = localProperties.getProperty("release.storeFile")
        if (storePath != null) {
            create("release") {
                storeFile = file(storePath)
                storePassword = localProperties.getProperty("release.storePassword")
                keyAlias = localProperties.getProperty("release.keyAlias")
                keyPassword = localProperties.getProperty("release.keyPassword")
            }
        }
    }

    buildTypes {
        release {
            if (localProperties.getProperty("release.storeFile") != null) {
                signingConfig = signingConfigs.getByName("release")
            }
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.retrofit.serialization)

    implementation(libs.jsoup)
    implementation(libs.readability4j)

    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // Testes
    testImplementation(libs.junit)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
