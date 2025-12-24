package com.example.findemy.data.repository

// Context digunakan untuk mengakses resource Android (contoh: DataStore, SharedPreferences)
import android.content.Context

// UserPreferences digunakan untuk mengambil token login yang disimpan secara lokal
import com.example.findemy.data.local.UserPreferences

// Model request untuk mengirim data jadwal ke server (POST / PUT)
import com.example.findemy.data.model.JadwalRequest

// Model data Jadwal (hasil response dari API)
import com.example.findemy.data.model.Jadwal

// Response list jadwal dari API
import com.example.findemy.data.model.JadwalListResponse

// Response single jadwal dari API (create / update)
import com.example.findemy.data.model.JadwalSingleResponse

// Response umum yang hanya berisi message (biasanya untuk delete)
import com.example.findemy.data.model.MessageResponse

// ApiClient berisi konfigurasi Retrofit + interceptor token
import com.example.findemy.data.remote.ApiClient

// Interface endpoint API jadwal
import com.example.findemy.data.remote.jadwal.JadwalService

// Digunakan untuk mengambil data Flow (token) secara sinkron
import kotlinx.coroutines.flow.first

// Digunakan untuk parsing error JSON dari server
import org.json.JSONObject

// Digunakan untuk menangani error HTTP (kode 4xx / 5xx)
import retrofit2.HttpException

// Digunakan untuk membaca response Retrofit
import retrofit2.Response

// Digunakan untuk menangani error koneksi jaringan
import java.io.IOException

/**
 * JadwalRepository
 * Berfungsi sebagai penghubung antara ViewModel dan API (Retrofit)
 * Menangani logic:
 * - Ambil token
 * - Panggil API
 * - Handle error
 */
class JadwalRepository(private val context: Context) {

    // Membuat instance API JadwalService menggunakan Retrofit
    private val api = ApiClient.retrofit.create(JadwalService::class.java)

    // UserPreferences digunakan untuk mengambil token login
    private val userPreferences = UserPreferences(context)

    /** ===========================
     *  GET Jadwals
     *  Mengambil daftar jadwal berdasarkan hari (opsional)
     *  =========================== */
    suspend fun getJadwals(hari: String? = null): Result<JadwalListResponse> {
        return try {
            // Mengambil token dari DataStore
            val token = userPreferences.token.first()

            // Jika token kosong, user harus login ulang
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("Token tidak ditemukan. Silakan login kembali."))
            }

            // Set token ke ApiClient agar dikirim ke header Authorization
            ApiClient.setToken(token)

            // Panggil endpoint GET /jadwal
            val response = api.getJadwals(hari)

            // Jika response sukses (HTTP 200–299)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Data tidak ditemukan"))
                }
            } else {
                // Jika gagal, ambil pesan error dari response
                val errorMessage = parseErrorMessage(response)
                Result.failure(Exception(errorMessage))
            }

        } catch (e: HttpException) {
            // Error dari server (misal 401, 403, 500)
            val errorMessage = parseHttpException(e)
            Result.failure(Exception(errorMessage))

        } catch (e: IOException) {
            // Error jaringan (tidak ada internet, timeout)
            Result.failure(Exception("Tidak dapat terhubung ke server"))

        } catch (e: Exception) {
            // Error umum lainnya
            Result.failure(Exception(e.message ?: "Terjadi kesalahan"))
        }
    }

    /** ===========================
     *  CREATE Jadwal
     *  Menambahkan jadwal baru ke server
     *  =========================== */
    suspend fun createJadwal(request: JadwalRequest): Result<Jadwal> {
        return try {
            val token = userPreferences.token.first()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("Token tidak ditemukan. Silakan login kembali."))
            }

            ApiClient.setToken(token)

            // Panggil endpoint POST /jadwal
            val response = api.createJadwal(request)

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception("Gagal membuat jadwal"))
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
     *  UPDATE Jadwal
     *  Mengubah data jadwal berdasarkan ID
     *  =========================== */
    suspend fun updateJadwal(id: Int, request: JadwalRequest): Result<Jadwal> {
        return try {
            val token = userPreferences.token.first()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("Token tidak ditemukan. Silakan login kembali."))
            }

            ApiClient.setToken(token)

            // Panggil endpoint PUT /jadwal/{id}
            val response = api.updateJadwal(id, request)

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception("Gagal mengupdate jadwal"))
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
     *  DELETE Jadwal
     *  Menghapus jadwal berdasarkan ID
     *  =========================== */
    suspend fun deleteJadwal(id: Int): Result<String> {
        return try {
            val token = userPreferences.token.first()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("Token tidak ditemukan. Silakan login kembali."))
            }

            ApiClient.setToken(token)

            // Panggil endpoint DELETE /jadwal/{id}
            val response = api.deleteJadwal(id)

            if (response.isSuccessful) {
                val body = response.body()
                Result.success(body?.message ?: "Jadwal berhasil dihapus")
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
