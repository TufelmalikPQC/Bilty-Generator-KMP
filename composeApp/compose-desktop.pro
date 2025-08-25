# ✅ Keep all your app classes and methods (entire package)
-keep class com.bilty.generator.** { *; }

# ✅ Keep all Kotlin classes and metadata
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }

# ✅ Keep coroutines (used in LaunchedEffect, remember, etc.)
-keep class kotlinx.coroutines.** { *; }

# ✅ Keep Compose UI and runtime
-keep class androidx.compose.** { *; }
-keep class org.jetbrains.compose.** { *; }
-keep class org.jetbrains.skiko.** { *; }

# ✅ Keep KCEF and native bindings
-keep class dev.datlag.kcef.** { *; }

# ✅ Keep JVM annotations and reflection
-keep class org.jetbrains.annotations.** { *; }
-keep class kotlin.reflect.** { *; }

# ✅ Keep Compose tooling (optional, for preview support)
-keep class androidx.compose.ui.tooling.preview.** { *; }

# ✅ Keep entry point (if needed explicitly)
-keep class com.bilty.generator.** { public static void main(java.lang.String[]); }

# ✅ Don't warn about missing classes
-dontwarn **

# ✅ Disable shrinking and obfuscation (safe mode)
-dontoptimize
-dontobfuscate
