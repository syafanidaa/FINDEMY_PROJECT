// Top-level Gradle file (Project level)
// Berisi konfigurasi global untuk semua module (app / library)

plugins {
    // Plugin Android Application (belum diterapkan, hanya dideklarasikan)
    alias(libs.plugins.android.application) apply false

    // Plugin Kotlin untuk Android
    alias(libs.plugins.kotlin.android) apply false

    // Plugin Kotlin Compose untuk Jetpack Compose
    alias(libs.plugins.kotlin.compose) apply false
}
