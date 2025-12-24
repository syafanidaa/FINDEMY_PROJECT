plugins {
    // Plugin utama Android Application
    alias(libs.plugins.android.application)
    // Plugin Kotlin untuk Android
    alias(libs.plugins.kotlin.android)
    // Plugin Kotlin untuk Jetpack Compose
    alias(libs.plugins.kotlin.compose)
}

android {
    // Namespace / package utama aplikasi
    namespace = "com.example.findemy"
    // SDK yang digunakan saat compile
    compileSdk = 36

    defaultConfig {
        // ID unik aplikasi
        applicationId = "com.example.findemy"
        // Minimal Android yang didukung
        minSdk = 24
        // Target Android
        targetSdk = 36
        // Versi internal aplikasi
        versionCode = 1
        // Versi yang ditampilkan ke user
        versionName = "1.0"

        // Runner untuk instrument test
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Konfigurasi signing untuk build release
    signingConfigs {
        create("release") {
            storeFile = file("findemy_keystore.jks")
            storePassword = "password"
            keyAlias = "release"
            keyPassword = "password"
        }
    }

    buildTypes {
        release {
            // Tidak menggunakan minify (obfuscation)
            isMinifyEnabled = false
            // Menggunakan signing release
            signingConfig = signingConfigs.getByName("release")
            // File ProGuard
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Konfigurasi Java
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        // Aktifkan desugaring (java.time)
        isCoreLibraryDesugaringEnabled = true
    }

    // Konfigurasi Kotlin
    kotlinOptions {
        jvmTarget = "11"
    }

    // Aktifkan Jetpack Compose
    buildFeatures {
        compose = true
    }
}

dependencies {

    /* ================= ANDROID & COMPOSE ================= */
    implementation(libs.androidx.core.ktx) // Core Android KTX
    implementation(libs.androidx.lifecycle.runtime.ktx) // Lifecycle
    implementation(libs.androidx.activity.compose) // Activity Compose
