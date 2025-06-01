package com.dark.listapplicationsplugin

import android.content.Context
import android.widget.Toast
import com.dark.plugin_api.info.Plugin

class PluginImpl : Plugin {
    override fun getName(): String = "List Applications Plugin"
    override fun run(context: Context) {
        Toast.makeText(context, "List Applications Plugin", Toast.LENGTH_SHORT).show()
    }
}
