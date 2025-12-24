package com.example.findemy.data.repository

// Context digunakan untuk mengakses DataStore (UserPreferences)
import android.content.Context

// UserPreferences untuk mengambil token login user
import com.example.findemy.data.local.UserPreferences

// Model response pesan umum dari API
import com.example.findemy.data.model.MessageResponse

// Model data Tugas (1 item)
import com.example.findemy.data.model.Tugas

// Model response daftar tugas
import com.example.findemy.data.model.TugasListResponse

// Model request untuk create & update tugas
import com.example.findemy.data.model.TugasRequest

// ApiClient berisi konfigurasi Retrofit + Authorization header
import com.example.findemy.data.remote.ApiClient

// Interface endpoint API Tugas
import com.example.findemy.data.remote.tugas.TugasService

// Untuk mengambil nilai Flow (token) secara langsung
import kotlinx.coroutines.flow.first

// Untuk parsing error response berbentuk JSON
import org.json.JSONObject

// Untuk menangani error HTTP (401, 403, 500, dll)
import retrofit2.HttpException

// Untuk membaca response Retrofit
import retrofit2.Response

// Untuk menangani error koneksi (tidak ada internet, timeout)
import java.io.IOException

/**
 * TugasRepository
 * Repository sebagai penghubung ViewModel ↔ API Tugas
 * Bertugas:
 * - Mengambil token user
 * - Memanggil endpoint API
 * - Mengelola response dan error
 */
class TugasRepository(private val context: Context) {

    // Membuat instance TugasService menggunakan Retrofit
    private val api = ApiClient.retrofit.create(TugasService::class.java)

    // Inisialisasi UserPreferences untuk akses token
    private val userPreferences = UserPreferences(context)

    /** ===========================
     *  GET Tugass
     *  Mengambil daftar tugas, bisa difilter berdasarkan status
     *  =========================== */
    suspend fun getTugass(status: String? = null): Result<TugasListResponse> {
        return try {
            // Ambil token login dari DataStore
            val token = userPreferences.token.first()

            // Jika token tidak ada, user harus login ulang
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("Token tidak ditemukan. Silakan login kembali."))
            }

            // Set token ke header Authorization
            ApiClient.setToken(token)

            // Panggil endpoint GET /tugas
            val response = api.getTugass(status)

            // Jika response sukses
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Data tidak ditemukan"))
                }
            } else {
                // Ambil pesan error dari response
                val errorMessage = parseErrorMessage(response)
                Result.failure(Exception(errorMessage))
            }

        } catch (e: HttpException) {
            // Error dari server (HTTP error)
            val errorMessage = parseHttpException(e)
            Result.failure(Exception(errorMessage))

        } catch (e: IOException) {
            // Error jaringan
            Result.failure(Exception("Tidak dapat terhubung ke server"))

        } catch (e: Exception) {
            // Error lainnya
            Result.failure(Exception(e.message ?: "Terjadi kesalahan"))
        }
    }

    /** ===========================
     *  CREATE Tugas
     *  Menambahkan tugas baru
     *  =========================== */
    suspend fun createTugas(request: TugasRequest): Result<Tugas> {
        return try {
            val token = userPreferences.token.first()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("Token tidak ditemukan. Silakan login kembali."))
            }

            ApiClient.setToken(token)

            // Panggil endpoint POST /tugas
            val response = api.createTugas(request)

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception("Gagal membuat tugas"))
                }
            } else {
                val errorMessage = parseErrorMessage(response)
                Result.failure(Exception(errorMessage))
            }

        } catch (e: HttpException) {
            val errorMessage = parseHttpException(e)
            Result.failure(Exception(errorMessage))

        } catch (e: IOException) {
            Result.failure(Exception("Tidak dapat terhubung ke server"))

        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Terjadi kesalahan"))
        }
    }

    /** ===========================
     *  UPDATE Tugas
     *  Mengubah data tugas berdasarkan ID
     *  =========================== */
    suspend fun updateTugas(id: Int, request: TugasRequest): Result<Tugas> {
        return try {
            val token = userPreferences.token.first()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("Token tidak ditemukan. Silakan login kembali."))
            }

            ApiClient.setToken(token)

            // Panggil endpoint PUT /tugas/{id}
            val response = api.updateTugas(id, request)

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception("Gagal mengupdate tugas"))
                }
            } else {
                val errorMessage = parseErrorMessage(response)
                Result.failure(Exception(errorMessage))
            }

        } catch (e: HttpException) {
            val errorMessage = parseHttpException(e)
            Result.failure(Exception(errorMessage))

        } catch (e: IOException) {
            Result.failure(Exception("Tidak dapat terhubung ke server"))

        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Terjadi kesalahan"))
        }
    }

    /** ===========================
     *  DELETE Tugas
     *  Menghapus tugas berdasarkan ID
     *  =========================== */
    suspend fun deleteTugas(id: Int): Result<String> {
        return try {
            val token = userPreferences.token.first()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("Token tidak ditemukan. Silakan login kembali."))
            }

            ApiClient.setToken(token)

            // Panggil endpoint DELETE /tugas/{id}
            val response = api.deleteTugas(id)

            if (response.isSuccessful) {
                val body = response.body()
                Result.success(body?.message ?: "Tugas berhasil dihapus")
            } else {
                val errorMessage = parseErrorMessage(response)
                Result.failure(Exception(errorMessage))
            }

        } catch (e: HttpException) {
            val errorMessage = parseHttpException(e)
            Result.failure(Exception(errorMessage))

        } catch (e: IOException) {
            Result.failure(Exception("Tidak dapat terhubung ke server"))

        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Terjadi kesalahan"))
        }
    }

    /** ===========================
     *  Helper Function
     *  Mengambil pesan error dari response body (JSON)
     *  =========================== */
    private fun parseErrorMessage(response: Response<*>): String {
        return try {
            val errorBody = response.errorBody()?.string()
            if (errorBody != null) {
                val json = JSONObject(errorBody)
                json.optString("message", "Terjadi kesalahan pada server")
            } else {
                "Terjadi kesalahan pada server"
            }
        } catch (e: Exception) {
            "Terjadi kesalahan pada server"
        }
    }

    /** ===========================
     *  Helper Function
     *  Mengambil pesan error dari HttpException
     *  =========================== */
    private fun parseHttpException(e: HttpException): String {
        return try {
            val errorBody = e.response()?.errorBody()?.string()
            if (errorBody != null) {
                val json = JSONObject(errorBody)
                json.optString("message", "Terjadi kesalahan pada server")
            } else {
                "Terjadi kesalahan pada server"
            }
        } catch (ex: Exception) {
            "Terjadi kesalahan pada server"
        }
    }
}
