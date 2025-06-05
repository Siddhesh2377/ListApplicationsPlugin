# NeuroVerse Plugin SDK

This repository provides the official SDK and documentation to build custom **NeuroVerse plugins** — standalone APKs that extend the capabilities of the NeuroVerse AI assistant on Android.

---

## 🔍 What is This?
This is **not** the main NeuroVerse app. This is a plugin development kit for third-party developers to:
- Create Android-based plugin APKs
- Use full Android API support
- Package them with a manifest.json file
- Load them dynamically at runtime using the NeuroVerse plugin engine

---

## 🚀 How It Works

NeuroVerse dynamically loads plugins using `DexClassLoader` and routes commands to them via AI. Each plugin is a self-contained APK with a `manifest.json` file describing its name, version, permissions, and entry point.

Your plugin must implement the shared `Plugin` interface defined in `plugin-api.jar`:

```kotlin
open class Plugin(protected val context: Context)  {
    open fun getName(): String = "none"
    open fun onStart() {}
    open fun submitAiRequest(prompt: String): JSONObject = JSONObject()
    open fun onAiResponse(response: JSONObject) {}
    open fun onStop() {}
}
```

---

## 📦 Plugin Structure

Every plugin must be packaged like this:

```
MyPlugin.zip
├── plugin.apk
└── manifest.json
```

### manifest.json Example
```json
{
  "name": "AppLauncherPlugin",
  "description": "Launches any app by name",
  "main": "com.example.plugin.AppLauncherPlugin",
  "plugin-api-version": "1.0",
  "permissions": ["LAUNCH_APP"]
}
```

---

## 🧑‍💻 Steps to Build Your Plugin

1. Clone this repo or download `plugin-api.jar`
2. Create a new Android project in **Android Studio**
3. Add `plugin-api.jar` to your plugin project’s `libs/` folder
4. Extend the `Plugin` base class and override required methods
5. Build your APK (`plugin.apk`)
6. Create a `manifest.json` describing your plugin
7. Zip both `plugin.apk` + `manifest.json` → `MyPlugin.zip`
8. Import it in NeuroVerse using the Plugin Manager

---

## 🔧 Tools and Support
- Works with Kotlin or Java
- Compatible with libraries like Retrofit, Room, etc.
- Full Android API access

---

## 📁 Sample Plugin
> A complete starter template will be released soon.

---

## 💡 Tips
- Keep your plugin’s permissions minimal
- Make sure your `main` class in `manifest.json` matches the classpath
- The plugin will be run inside the NeuroVerse app context

---

## 📬 Contributing / Feedback
- Issues and PRs welcome!
- Contact the core NeuroVerse team if you’d like your plugin featured in the public plugin marketplace.

---
