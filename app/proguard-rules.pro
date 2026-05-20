# Add project specific ProGuard rules here.

# ---------- kotlinx.serialization ----------
# Keep generated serializer companion objects and the @Serializable classes
# in packages that contain serializable models (DTOs and navigation routes).
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class **$$serializer {
    <fields>;
    <methods>;
}
-keepclassmembers class com.example.todoist.data.remote.dto.** {
    *** Companion;
}
-keepclasseswithmembers class com.example.todoist.data.remote.dto.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.example.todoist.data.remote.dto.**$$serializer { *; }

-keepclassmembers class com.example.todoist.presentation.navigation.** {
    *** Companion;
}
-keepclasseswithmembers class com.example.todoist.presentation.navigation.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.example.todoist.presentation.navigation.**$$serializer { *; }

# ---------- Ktor ----------
-dontwarn io.ktor.**
-dontwarn org.slf4j.**
-keep class io.ktor.client.engine.android.** { *; }

# ---------- Room ----------
-keep class androidx.room.** { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-keep @androidx.room.Database class * { *; }
-keepclassmembers class * {
    @androidx.room.* <methods>;
}

# ---------- Koin ----------
-dontwarn org.koin.**

# ---------- Coroutines ----------
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.flow.** { *; }
