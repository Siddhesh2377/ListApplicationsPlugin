package com.dark.listapplicationsplugin

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.Toast
import com.dark.plugin_api.info.Plugin

class PluginImpl(override val context: Context) : Plugin {

    override fun getName(): String = "List Applications Plugin"

    override fun run() {
        Toast.makeText(context, "List Applications Plugin", Toast.LENGTH_SHORT).show()
        val apps = listApps(context)
        apps.forEach { app ->
            Log.d("Plugin", "App: ${app.name} - ${app.packageName}")
        }
    }

    fun listApps(context: Context): List<AppInfo> {
        val packageManager = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val apps =
            packageManager.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0))
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


