package com.example.findemy.core.navigation
// Package ini berisi pengaturan navigasi utama aplikasi Findemy

import androidx.compose.runtime.Composable
// Digunakan untuk menandai fungsi Composable (UI Jetpack Compose)

import androidx.navigation.NavHostController
// Controller untuk mengatur perpindahan antar screen (navigate, popBackStack, dll)

import androidx.navigation.NavType
// Digunakan untuk menentukan tipe data parameter navigasi (String, Int, dll)

import androidx.navigation.compose.NavHost
// Container utama navigasi Compose

import androidx.navigation.compose.composable
// Digunakan untuk mendefinisikan setiap route / halaman

import androidx.navigation.navArgument
// Digunakan untuk mendefinisikan parameter (argument) pada route

// Import semua screen yang akan digunakan dalam navigasi
import com.example.findemy.ui.about.AboutScreen
import com.example.findemy.ui.auth.forgot.ForgotPasswordScreen
import com.example.findemy.ui.auth.forgot.ResetPasswordScreen
import com.example.findemy.ui.auth.forgot.VerifyCodeScreen
import com.example.findemy.ui.splash.SplashScreen
import com.example.findemy.ui.onboarding.OnboardingScreen
import com.example.findemy.ui.auth.login.LoginScreen
import com.example.findemy.ui.auth.register.RegisterScreen
import com.example.findemy.ui.base.BaseScreen
import com.example.findemy.ui.privacy.PrivacyScreen
import com.example.findemy.ui.rekening.RekeningPage
import com.example.findemy.ui.term.TermScreen

/**
 * Fungsi MainNavigation
 * ---------------------
 * Fungsi ini berperan sebagai pusat navigasi aplikasi.
 * Semua perpindahan halaman (screen) didefinisikan di sini.
 */
@Composable
fun MainNavigation(navController: NavHostController) {

    // NavHost adalah container utama navigasi Compose
    // startDestination menentukan halaman pertama saat aplikasi dibuka
    NavHost(navController = navController, startDestination = "splash") {

        /**
         * Route: splash
         * ----------------
         * Halaman splash screen yang muncul pertama kali
         * Biasanya digunakan untuk loading, cek login, dll
         */
        composable("splash") {
            SplashScreen(navController)
        }

        /**
         * Route: onboarding
         * ------------------
         * Halaman onboarding untuk user baru
         */
        composable("onboarding") {
            OnboardingScreen(navController)
        }

        /**
         * Route: login
         * ------------
         * Halaman login user
         */
        composable("login") {
            LoginScreen(navController)
        }

        /**
         * Route: register
         * ---------------
         * Halaman pendaftaran akun baru
         */
        composable("register") {
            RegisterScreen(navController)
        }

        /**
         * Route: about
         * ------------
         * Halaman informasi tentang aplikasi
         */
        composable("about") {
            AboutScreen(navController)
        }

        /**
         * Route: privacy
         * --------------
         * Halaman kebijakan privasi aplikasi
         */
        composable("privacy") {
            PrivacyScreen(navController)
        }

        /**
         * Route: term
         * -----------
         * Halaman syarat dan ketentuan penggunaan aplikasi
         */
        composable("term") {
            TermScreen(navController)
        }

        /**
         * Route: forgot_password
         * ----------------------
         * Halaman untuk memasukkan email saat lupa password
         */
        composable("forgot_password") {
            ForgotPasswordScreen(navController)
        }

        /**
         * Route: verify_code/{email}
         * ---------------------------
         * Halaman verifikasi kode OTP
         * Menerima parameter email dari halaman sebelumnya
         */
        composable(
            route = "verify_code/{email}",
            arguments = listOf(
                // Mendefinisikan bahwa parameter email bertipe String
                navArgument("email") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            // Mengambil nilai email dari argument navigation
            // Jika null, maka diisi string kosong
            val email = backStackEntry.arguments?.getString("email") ?: ""

            // Memanggil VerifyCodeScreen dan mengirimkan email
            VerifyCodeScreen(navController, email)
        }

        /**
         * Route: reset_password/{email}/{code}
         * ------------------------------------
         * Halaman reset password
         * Menerima dua parameter:
         * 1. email  -> email user
         * 2. code   -> kode verifikasi
         */
        composable(
            route = "reset_password/{email}/{code}",
            arguments = listOf(
                // Argument email bertipe String
                navArgument("email") { type = NavType.StringType },

                // Argument kode verifikasi bertipe String
                navArgument("code") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            // Mengambil email dari argument
            val email = backStackEntry.arguments?.getString("email") ?: ""

            // Mengambil kode verifikasi dari argument
            val code = backStackEntry.arguments?.getString("code") ?: ""

            // Memanggil ResetPasswordScreen dengan email dan kode
            ResetPasswordScreen(navController, email, code)
        }

        /**
         * Route: base
         * -----------
         * Halaman utama aplikasi setelah login
         * Biasanya berisi bottom navigation / dashboard
         */
        composable("base") {
            BaseScreen(navController)
        }

        /**
         * Route: rekening
         * ---------------
         * Halaman manajemen rekening / pembayaran
         */
        composable("rekening") {
            RekeningPage(navController)
        }
    }
}
