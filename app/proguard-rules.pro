# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep line numbers for debugging stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ===========================
# Kotlin & Serialization
# ===========================
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# Keep Kotlin Metadata for reflection
-keep class kotlin.Metadata { *; }
-keep class kotlin.reflect.** { *; }
-dontwarn kotlin.reflect.**

# Ignore missing Kotlin default implementations (legacy Kotlin 1.x)
-dontwarn kotlin.Cloneable$DefaultImpls
-dontwarn kotlin.**$DefaultImpls

# Keep all Kotlin default implementations for interfaces that exist
-keep class **$DefaultImpls { *; }

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep @Serializable classes
-keep,includedescriptorclasses class xyz.isekhon.drawabletuner.**$$serializer { *; }
-keepclassmembers class xyz.isekhon.drawabletuner.** {
    *** Companion;
}
-keepclasseswithmembers class xyz.isekhon.drawabletuner.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep Parcelize
-keep class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# ===========================
# Jetpack Compose
# ===========================
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-keepclassmembers class androidx.compose.** { *; }

# Keep Compose Compiler generated classes
-keep class androidx.compose.runtime.internal.ComposableLambdaImpl { *; }

# Navigation Compose
-keep class androidx.navigation.** { *; }
-keepnames class androidx.navigation.** { *; }

# ===========================
# Sora Editor
# ===========================
-keep class io.github.rosemoe.sora.** { *; }
-keep interface io.github.rosemoe.sora.** { *; }
-keepclassmembers class io.github.rosemoe.sora.** { *; }

# TextMate language support
-keep class org.eclipse.tm4e.** { *; }
-keep interface org.eclipse.tm4e.** { *; }

# ===========================
# XML Processing (dom4j)
# ===========================
-keep class org.dom4j.** { *; }
-keep interface org.dom4j.** { *; }
-dontwarn org.dom4j.**

# ===========================
# Gson
# ===========================
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep all model classes
-keep class xyz.isekhon.drawabletuner.data.model.** { *; }

# ===========================
# Application Specific
# ===========================
# Keep all data classes
-keepclassmembers class xyz.isekhon.drawabletuner.data.** {
    <init>(...);
    <fields>;
}

# Keep ViewModels
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keep class xyz.isekhon.drawabletuner.ui.viewmodel.** { *; }

# Keep Repository classes
-keep class xyz.isekhon.drawabletuner.data.repository.** { *; }

# ===========================
# General Android
# ===========================
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ===========================
# Optimization Settings
# ===========================
# Don't warn about missing classes
-dontwarn org.xmlpull.v1.**
-dontwarn org.slf4j.**
-dontwarn org.apache.**

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Preserve some attributes that may be required for reflection
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations
-keepattributes EnclosingMethod
-keepattributes Exceptions
-keepattributes InnerClasses
