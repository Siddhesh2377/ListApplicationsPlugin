package com.dark.listapplicationsplugin

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    fun Int.dp(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
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

    override fun onAiResponse(response: JSONObject): ViewGroup {
        val root = LinearLayout(context)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            // Margins: outside space
            setMargins(32, 16, 32, 16)  // left, top, right, bottom in pixels
        }

        // Apply layout params to root
        root.layoutParams = params

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

                root.apply {

                    setPadding(16, 16, 16, 16)

                    val listView = ListView(context)

                    listView.adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, apps.map { it.name })

                    listView.setOnItemClickListener { _, _, position, _ ->
                        val selectedApp = apps[position]
                        launchApp(context, selectedApp.packageName) {
                            Log.e("Plugin", "❌ Could not launch ${selectedApp.name}: $it")
                        }
                    }

                    Log.d("Plugin:View", "Added View")

                    addView(listView)
                }
                return root
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

        return root
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


