pluginManagement {
    // Mengatur repository yang digunakan untuk mengambil plugin Gradle
    repositories {
        google {
            // Membatasi plugin yang diambil hanya dari grup tertentu
            content {
                includeGroupByRegex("com\\.android.*")   // Plugin Android
                includeGroupByRegex("com\\.google.*")    // Plugin Google
                includeGroupByRegex("androidx.*")        // Plugin AndroidX
            }
        }
        mavenCentral()       // Repository utama untuk library Kotlin/Java
        gradlePluginPortal() // Repository resmi plugin Gradle
    }
}

dependencyResolutionManagement {
    // Mencegah penggunaan repository di level module (build.gradle app)
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    // Repository global untuk seluruh dependency project
    repositories {
        google()        // Library Android & Google
        mavenCentral()  // Library pihak ketiga
    }
}

// Nama root project Android Studio
rootProject.name = "FinDemy"

// Menyertakan module app ke dalam project
include(":app")
