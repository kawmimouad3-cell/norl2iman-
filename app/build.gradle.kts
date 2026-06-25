import java.net.URL
import java.net.HttpURLConnection
import java.net.URI

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.roborazzi)
  alias(libs.plugins.secrets)
}


android {
  namespace = "com.aistudio.quran.mwkpqz"
  compileSdk = 36

  defaultConfig {
    applicationId = "com.aistudio.quran.mwkpqz"
    minSdk = 24
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

//   signingConfigs {
//     create("release") {
//       val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/my-upload-key.jks"
//       storeFile = file(keystorePath)
//       storePassword = System.getenv("STORE_PASSWORD")
//       keyAlias = "upload"
//       keyPassword = System.getenv("KEY_PASSWORD")
//     }
//     create("debugConfig") {
//       storeFile = file("${rootDir}/debug.keystore")
//       storePassword = "android"
//       keyAlias = "androiddebugkey"
//       keyPassword = "android"
//     }
//   }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      // signingConfig = signingConfigs.getByName("release")
    }
    debug {
      // signingConfig = signingConfigs.getByName("debugConfig")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions { unitTests { isIncludeAndroidResources = true } }
}

// Configure the Secrets Gradle Plugin to use .env and .env.example files
// to match the convention used in Web projects.
secrets {
  propertiesFileName = ".env"
  defaultPropertiesFileName = ".env.example"
}

// Some unused dependencies are commented out below instead of being removed.
// This makes it easy to add them back in the future if needed.
dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(platform(libs.firebase.bom))
  // implementation(libs.accompanist.permissions)
  implementation(libs.androidx.activity.compose)
  // implementation(libs.androidx.camera.camera2)
  // implementation(libs.androidx.camera.core)
  // implementation(libs.androidx.camera.lifecycle)
  // implementation(libs.androidx.camera.view)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation("androidx.compose.ui:ui-text-google-fonts:1.6.7")
  implementation(libs.androidx.core.ktx)
  // implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  // implementation(libs.coil.compose)
  implementation(libs.converter.moshi)
  // implementation(libs.firebase.ai)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.logging.interceptor)
  implementation(libs.moshi.kotlin)
  implementation(libs.okhttp)
  implementation("org.jsoup:jsoup:1.17.2")
  implementation("com.google.android.gms:play-services-location:21.2.0")
  implementation("androidx.work:work-runtime-ktx:2.10.1")
  implementation(libs.retrofit)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  testImplementation(libs.roborazzi)
  testImplementation(libs.roborazzi.compose)
  testImplementation(libs.roborazzi.junit.rule)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  "ksp"(libs.androidx.room.compiler)
  "ksp"(libs.moshi.kotlin.codegen)
}

abstract class DownloadQuranFontTask : DefaultTask() {
    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun download() {
        val destFile = outputFile.get().asFile
        destFile.parentFile.mkdirs()
        println("Downloading authentic Quran font from official Alif Type repository...")
        var downloadSuccess = false
        try {
            val url = URI("https://raw.githubusercontent.com/aliftype/amiri/main/fonts/AmiriQuran.ttf").toURL()
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 15000
            conn.readTimeout = 15000
            if (conn.responseCode == 200) {
                conn.inputStream.use { input ->
                    destFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                println("Quran font downloaded successfully from Github repository, size: ${destFile.length()} bytes")
                downloadSuccess = true
            } else {
                throw GradleException("Failed to download main font: HTTP ${conn.responseCode}")
            }
        } catch (e: Exception) {
            println("Error downloading main font url: ${e.message}. Trying backup unpkg...")
        }

        if (!downloadSuccess) {
            try {
                val url = URI("https://unpkg.com/kfgqpc-uthmanic-script-hafs-regular@1.0.0/arabic.otf").toURL()
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 15000
                conn.readTimeout = 15000
                if (conn.responseCode == 200) {
                    conn.inputStream.use { input ->
                        destFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    println("Backup OTF Quran font downloaded successfully, size: ${destFile.length()} bytes")
                } else {
                    throw GradleException("Failed to download backup font: HTTP ${conn.responseCode}")
                }
            } catch (e2: Exception) {
                throw GradleException("Failed to download any Quran font: ${e2.message}", e2)
            }
        }
    }
}

val downloadQuranFont = tasks.register<DownloadQuranFontTask>("downloadQuranFont") {
    outputFile.set(layout.projectDirectory.file("src/main/res/font/quran_font.ttf"))
}

tasks.named("preBuild") {
    dependsOn(downloadQuranFont)
}
