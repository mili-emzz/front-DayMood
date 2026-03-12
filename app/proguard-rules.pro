# =============================================
# DayMood - ProGuard Rules para Release APK
# =============================================

# Stack traces legibles
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# -- ATRIBUTOS GLOBALES --
-keepattributes Signature,Exceptions,*Annotation*,EnclosingMethod,InnerClasses

# -- RETROFIT + OKHTTP --
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keep class okhttp3.internal.publicsuffix.PublicSuffixDatabase { *; }

# -- GSON --
-dontwarn sun.misc.**
-keep class sun.misc.** { *; }
-keep class com.google.gson.** { *; }

# -- MODELOS (¡ESTO ES LO MÁS IMPORTANTE!) --
-keep class com.lumina.app_daymood.data.api.dto.** { *; }
-keep class com.lumina.app_daymood.domain.models.** { *; }

# -- FIREBASE --
-dontwarn com.google.firebase.**
-keep class com.google.firebase.** { *; }
-dontwarn com.google.android.gms.**
-keep class com.google.android.gms.** { *; }

# -- COROUTINES --
-dontwarn kotlinx.coroutines.**
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# -- OTROS --
-dontwarn coil.**
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }
