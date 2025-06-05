# Keep your plugin implementation and any inner classes
-keep class com.dark.listapplicationsplugin.** { *; }

# Keep your plugin interface (dynamic load needs this)
-keep class com.dark.plugin_api.info.Plugin { *; }
-keep interface com.dark.plugin_api.info.Plugin

# Keep the plugin version info classes
-keep class com.dark.plugin_api.info.** { *; }

# Keep Kotlin metadata and internal coroutine support
-keep class kotlin.** { *; }
-keepclassmembers class kotlin.Metadata { *; }
-dontwarn kotlin.**

# If you're using JSON serialization via reflection
-keep class org.json.JSONObject { *; }
-keep class org.json.JSONArray { *; }

# Required to avoid stripping context/getPackageManager-based reflection
-keep class android.content.pm.PackageManager { *; }
-keep class android.content.Intent { *; }

# Prevent AndroidX and Jetpack stripping in plugins
-dontwarn androidx.**
-keep class androidx.** { *; }

# Ensure AppInfo (your internal data model) is preserved
-keepclassmembers class com.dark.listapplicationsplugin.PluginImpl$AppInfo { *; }

# If you use annotations
-keepattributes *Annotation*, Signature

# Keep all public methods for reflection or method access
-keepclassmembers class * {
    public *;
}
