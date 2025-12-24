package com.example.findemy.data.model
// Package untuk model data autentikasi (forgot password)

/**
 * Request untuk mengirim email lupa password
 */
data class ForgotPasswordRequest(
    val email: String   // Email user
)

/**
 * Request untuk verifikasi kode
 */
data class VerifyCodeRequest(
    val email: String,  // Email user
    val code: String    // Kode verifikasi
)

/**
 * Request untuk reset password
 */
data class ResetPasswordRequest(
    val email: String,     // Email user
    val code: String,      // Kode verifikasi
    val password: String  // Password baru
)
