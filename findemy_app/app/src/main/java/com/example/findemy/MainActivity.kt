package com.example.findemy

// Digunakan untuk lifecycle Activity Android
import android.os.Bundle

// Base Activity khusus Jetpack Compose
import androidx.activity.ComponentActivity

// Untuk menampilkan UI berbasis Compose
import androidx.activity.compose.setContent

// Untuk membuat dan mengingat NavController (navigasi Compose)
import androidx.navigation.compose.rememberNavController

// Navigation graph utama aplikasi
import com.example.findemy.core.navigation.MainNavigation

// Theme utama aplikasi FinDemy
import com.example.findemy.ui.theme.FinDemyTheme

/**
 * MainActivity
 * Activity utama dan entry point aplikasi FinDemy
 * Bertugas:
 * - Mengatur UI menggunakan Jetpack Compose
 * - Menerapkan tema aplikasi
 * - Menyediakan NavController untuk navigasi antar screen
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setContent digunakan untuk menampilkan UI Compose
        setContent {

            // Membungkus seluruh UI dengan tema FinDemy
            FinDemyTheme {

                // NavController untuk mengatur perpindahan antar screen
                val navController = rememberNavController()

                // Menjalankan navigation graph utama aplikasi
                MainNavigation(navController)
            }
        }
    }
}
