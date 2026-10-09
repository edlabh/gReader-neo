package com.greader.neo

import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Smoke test do fluxo crítico: o app abre e a Home renderiza a barra superior
 * (independente de ter sessão ou não).
 *
 * Requer device/emulador conectado:
 *   ./gradlew :app:connectedDebugAndroidTest
 */
@RunWith(AndroidJUnit4::class)
class HomeSmokeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun homeShowsTopBar() {
        // O ícone de sync existe nos dois layouts (telefone e tablet 2 painéis).
        val sync = composeRule.activity.getString(R.string.cd_sync)
        composeRule.onNodeWithContentDescription(sync).assertExists()
    }
}
