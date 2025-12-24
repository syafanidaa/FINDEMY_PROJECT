package com.example.findemy.data.remote.auth
// Package untuk service API autentikasi

import com.example.findemy.data.model.LoginRequest
import com.example.findemy.data.model.AuthResponse
import com.example.findemy.data.model.ForgotPasswordRequest
import com.example.findemy.data.model.ForgotPasswordResponse
import com.example.findemy.data.model.RegisterRequest
import com.example.findemy.data.model.ResetPasswordRequest
import com.example.findemy.data.model.VerifyCodeRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Interface AuthService
 * Digunakan Retrofit untuk komunikasi API autentikasi
 */
interface AuthService {

    // Endpoint login user
    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    // Endpoint registrasi user
    @POST("register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    // Endpoint lupa password
    @POST("forgot-password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): Response<ForgotPasswordResponse>

    // Endpoint verifikasi kode
    @POST("verify-code")
    suspend fun verifyCode(
        @Body request: VerifyCodeRequest
    ): Response<ForgotPasswordResponse>

    // Endpoint reset password
    @POST("reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<ForgotPasswordResponse>
}
