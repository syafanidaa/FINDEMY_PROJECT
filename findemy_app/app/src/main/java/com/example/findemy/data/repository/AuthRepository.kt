package com.example.findemy.data.repository

// Model request & response untuk autentikasi user
import com.example.findemy.data.model.LoginRequest
import com.example.findemy.data.model.RegisterRequest
import com.example.findemy.data.model.AuthResponse
import com.example.findemy.data.model.ForgotPasswordRequest
import com.example.findemy.data.model.ForgotPasswordResponse
import com.example.findemy.data.model.ResetPasswordRequest
import com.example.findemy.data.model.VerifyCodeRequest

// ApiClient sebagai konfigurasi Retrofit (base URL, interceptor, dll)
import com.example.findemy.data.remote.ApiClient

// Service endpoint autentikasi
import com.example.findemy.data.remote.auth.AuthService

// Digunakan untuk membaca error response dari backend
import org.json.JSONObject

// Exception untuk error HTTP dan jaringan
import retrofit2.HttpException
import java.io.IOException

// AuthRepository menangani proses autentikasi dan komunikasi API
class AuthRepository {

    // Membuat instance AuthService dari Retrofit
    private val api = ApiClient.retrofit.create(AuthService::class.java)

    // Proses login user menggunakan email dan password
    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                // Berhasil login
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Response kosong dari server"))
            } else {
                // Gagal login, ambil pesan error dari backend
                val message = try {
                    JSONObject(response.errorBody()?.string() ?: "{}")
                        .optString("message", "Login gagal")
                } catch (e: Exception) {
                    "Login gagal"
                }
                Result.failure(Exception(message))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("Login gagal"))
        } catch (e: IOException) {
            Result.failure(Exception("Tidak dapat terhubung ke server"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Proses registrasi user baru
    suspend fun register(name: String, email: String, password: String): Result<AuthResponse> {
        return try {
            val response = api.register(RegisterRequest(name, email, password))
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Response kosong dari server"))
            } else {
                // Menangani error validasi (misalnya email sudah terdaftar)
                val message = try {
                    val json = JSONObject(response.errorBody()?.string() ?: "{}")
                    if (json.has("errors")) {
                        val errors = json.getJSONObject("errors")
                        val key = errors.keys().asSequence().first()
                        errors.getJSONArray(key).getString(0)
                    } else {
                        json.optString("message", "Registrasi gagal")
                    }
                } catch (e: Exception) {
                    "Registrasi gagal"
                }
                Result.failure(Exception(message))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("Registrasi gagal"))
        } catch (e: IOException) {
            Result.failure(Exception("Tidak dapat terhubung ke server"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Mengirim kode verifikasi reset password ke email
    suspend fun forgotPassword(email: String): Result<ForgotPasswordResponse> {
        return try {
            val response = api.forgotPassword(ForgotPasswordRequest(email))
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Response kosong dari server"))
            } else {
                val message = try {
                    JSONObject(response.errorBody()?.string() ?: "{}")
                        .getJSONObject("meta")
                        .optString("message", "Gagal mengirim kode")
                } catch (e: Exception) {
                    "Gagal mengirim kode"
                }
                Result.failure(Exception(message))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Tidak dapat terhubung ke server"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Verifikasi kode reset password
    suspend fun verifyCode(email: String, code: String): Result<ForgotPasswordResponse> {
        return try {
            val response = api.verifyCode(VerifyCodeRequest(email, code))
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Response kosong dari server"))
            } else {
                val message = try {
                    JSONObject(response.errorBody()?.string() ?: "{}")
                        .getJSONObject("meta")
                        .optString("message", "Kode tidak valid")
                } catch (e: Exception) {
                    "Kode tidak valid"
                }
                Result.failure(Exception(message))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Tidak dapat terhubung ke server"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Reset password menggunakan kode verifikasi
    suspend fun resetPassword(
        email: String,
        code: String,
        password: String
    ): Result<ForgotPasswordResponse> {
        return try {
            val response = api.resetPassword(
                ResetPasswordRequest(email, code, password)
            )
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Response kosong dari server"))
            } else {
                val message = try {
                    JSONObject(response.errorBody()?.string() ?: "{}")
                        .getJSONObject("meta")
                        .optString("message", "Gagal reset password")
                } catch (e: Exception) {
                    "Gagal reset password"
                }
                Result.failure(Exception(message))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Tidak dapat terhubung ke server"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
