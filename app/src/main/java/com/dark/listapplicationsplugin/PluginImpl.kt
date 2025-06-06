package com.dark.listapplicationsplugin

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.dark.plugin_api.info.Plugin
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

class PluginImpl(context: Context) : Plugin(context) {

    override fun getName(): String = ""

    override fun onStart() {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, "List Applications Plugin", Toast.LENGTH_SHORT).show()
        }

//        val apps = listApps(context)
//        apps.forEach { app ->
//            Log.d("Plugin", "App: ${app.name} - ${app.packageName}")
//        }

        val result = try {
            URL("https://www.google.com").readText()
        } catch (e: Exception) {
            "❌ Failed: ${e.message}"
        }
        Log.d("Plugin", result)

    }


    override fun onStop() {
        Log.d(getName(), "onStop called()")
    }

    override fun submitAiRequest(prompt: String): JSONObject {

        val installedApps = listApps(context)
        val appListText = installedApps.joinToString(separator = "\n") { "- ${it.name}" }

        return JSONObject().apply {
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put(
                        "content",
                        """
                    You are an AI assistant that responds ONLY with valid JSON output for Android app-related actions. 
                    Based on the user's prompt, choose the appropriate command format.

                    Valid command formats:
                    1. {"data": "App Name", "action": "open_app"}
                    2. {"data": "App Name", "action": "check_if_exists"}
                    3. {"data": "$appListText", "action": "list_installed_apps"}

                    Do not return any extra text. Always return a single JSON object that follows one of the above formats exactly.
                    
                    📱 Available Apps on Device:
                    $appListText
                    """.trimIndent()
                    )
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            })
        }
    }

    override fun onAiResponse(response: JSONObject) {
        Log.d("Plugin", "onAiResponse called() $response")

        when (response.getString("action")) {
            "list_installed_apps" -> {
                val apps = listApps(context)
                val appNames = apps.joinToString(separator = "\n") { "- ${it.name}" }

                Log.i("Plugin", "📱 Installed Apps:\n$appNames")

                // Optional: Show in a Toast (main thread only)
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "Found ${apps.size} apps", Toast.LENGTH_SHORT).show()
                }

                // Optional: return this data back to host app if you support it
            }

            "open_app" -> {
                val appName = response.optString("data")
                val apps = listApps(context)
                val target = apps.find { it.name.equals(appName, ignoreCase = true) }

                if (target != null) {
                    launchApp(context, target.packageName) {
                        Log.e("Plugin", "❌ Could not launch $appName: $it")
                    }
                } else {
                    Log.w("Plugin", "⚠️ App '$appName' not found on device.")
                }
            }

            "check_if_exists" -> {
                val appName = response.optString("data")
                val apps = listApps(context)
                val exists = apps.any { it.name.equals(appName, ignoreCase = true) }

                Log.i("Plugin", if (exists) "✅ App '$appName' is installed." else "❌ App '$appName' is not installed.")
            }

            else -> {
                Log.w("Plugin", "⚠️ Unknown action: ${response.optString("action")}")
            }
        }
    }



    fun listApps(context: Context): List<AppInfo> {
        val packageManager = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val apps = packageManager.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0))
        return apps.map { resolveInfo ->
            val appName = resolveInfo.loadLabel(packageManager).toString()
            val packageName = resolveInfo.activityInfo.packageName
            val icon = resolveInfo.loadIcon(packageManager)
            AppInfo(appName, packageName, icon)
        }
    }

    fun launchApp(context: Context, packageName: String, onError: (err: String) -> Unit) {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            context.startActivity(launchIntent)
        } else {
            onError("App not found")
        }
    }

    data class AppInfo(
        val name: String,
        val packageName: String,
        val icon: Drawable
    )
}


