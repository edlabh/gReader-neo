# Regras ProGuard/R8 — gReader Neo
# kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**

-keepclassmembers class **$$serializer {
    *** descriptor;
}
-keepclasseswithmembers class com.greader.neo.data.feedly.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Retrofit
-keepattributes Signature, RuntimeVisibleAnnotations, AnnotationDefault
-dontwarn okhttp3.**
-dontwarn retrofit2.**

# slf4j (dependência transitiva da Readability4J) sem binding em runtime
-dontwarn org.slf4j.**
-dontwarn org.slf4j.impl.StaticLoggerBinder
