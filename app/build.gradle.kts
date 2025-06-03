import java.io.FileNotFoundException
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.dark.listapplicationsplugin"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.dark.listapplicationsplugin"
        minSdk = 33
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

//    signingConfigs {
//        create("debug") {
//            storeFile = file("${rootDir}/debug.keystore")
//            storePassword = "android"
//            keyAlias = "androiddebugkey"
//            keyPassword = "android"
//        }
//    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        create("plugin") {
            initWith(getByName("debug"))

            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true

            signingConfig = signingConfigs.getByName("debug")

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {



    compileOnly(files("libs/plugin-api-1.0.0.jar"))

    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.0")


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}


tasks.register("packagePluginZip") {
    group = "build"
    description = "Builds plugin APK and zips it as plugin.jar with external manifest.json"

    dependsOn("packagePlugin")

    doLast {
        val buildDir = layout.buildDirectory.get().asFile
        val apkDir = File(buildDir, "outputs/apk/plugin")
        val apkFile = apkDir.listFiles()?.firstOrNull { it.extension == "apk" }
            ?: throw FileNotFoundException("Plugin APK not found in $apkDir")

        val manifestFile = File(project.projectDir, "plugin_manifest/manifest.json")
        if (!manifestFile.exists()) throw FileNotFoundException("manifest.json not found at: $manifestFile")

        val outputZip = File(buildDir, "list_applications_plugin.zip")

        ZipOutputStream(outputZip.outputStream()).use { zip ->
            listOf(
                apkFile to "plugin.jar",
                manifestFile to "manifest.json"
            ).forEach { (file, name) ->
                zip.putNextEntry(ZipEntry(name))
                file.inputStream().use { it.copyTo(zip) }
                zip.closeEntry()
            }
        }

        println("✅ Plugin ZIP created at: ${outputZip.absolutePath}")
    }
}
