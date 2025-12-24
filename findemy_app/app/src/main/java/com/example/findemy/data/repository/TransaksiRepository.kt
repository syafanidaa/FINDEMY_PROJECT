package com.example.findemy.data.repository

// Context digunakan untuk mengakses resource Android (misalnya DataStore)
import android.content.Context

// UserPreferences digunakan untuk mengambil token login yang tersimpan
import com.example.findemy.data.local.UserPreferences

// Model data Transaksi (hasil response dari API)
import com.example.findemy.data.model.Transaksi

// Response list transaksi dari API
import com.example.findemy.data.model.TransaksiListResponse

// Model request transaksi (digunakan saat create & update)
import com.example.findemy.data.model.TransaksiRequest

// ApiClient berisi konfigurasi Retrofit dan interceptor Authorization
import com.example.findemy.data.remote.ApiClient

// Interface endpoint API transaksi
import com.example.findemy.data.remote.transaksi.TransaksiService

// Digunakan untuk mengambil nilai token dari Flow secara langsung
import kotlinx.coroutines.flow.first

// Digunakan untuk parsing error JSON dari server
import org.json.JSONObject

// Digunakan untuk menangani error HTTP (4xx, 5xx)
import retrofit2.HttpException

// Digunakan untuk membaca response Retrofit
import retrofit2.Response

// Digunakan untuk menangani error jaringan (internet mati, timeout)
import java.io.IOException

/**
 * TransaksiRepository
 * Repository bertugas sebagai penghubung antara ViewModel dan API
 * Menangani:
 * - Pengambilan token
 * - Pemanggilan API
 * - Penanganan error
 */
class TransaksiRepository(private val context: Context) {

    // Membuat instance TransaksiService menggunakan Retrofit
    private val api = ApiClient.retrofit.create(TransaksiService::class.java)

    // UserPreferences untuk mengambil token login user
    private val userPreferences = UserPreferences(context)

    /** ===========================
     *  GET Transaksis
     *  Mengambil daftar transaksi berdasarkan bulan dan tahun (opsional)
     *  =========================== */
    suspend fun getTransaksis(
        bulan: String? = null,
        tahun: String? = null
    ): Result<TransaksiListResponse> {
        return try {
            // Ambil token dari DataStore
            val token = userPreferences.token.first()

            // Jika token kosong, user harus login ulang
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("Token tidak ditemukan. Silakan login kembali."))
            }

            // Set token ke ApiClient agar dikirim di header Authorization
            ApiClient.setToken(token)

            // Panggil endpoint GET /transaksi
            val response = api.getTransaksis(bulan, tahun)

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
            // Error dari server (401, 403, 500, dll)
            val errorMessage = parseHttpException(e)
            Result.failure(Exception(errorMessage))

        } catch (e: IOException) {
            // Error koneksi jaringan
            Result.failure(Exception("Tidak dapat terhubung ke server"))

        } catch (e: Exception) {
            // Error umum lainnya
            Result.failure(Exception(e.message ?: "Terjadi kesalahan"))
        }
    }

    /** ===========================
     *  CREATE Transaksi
     *  Menambahkan transaksi baru ke server
     *  =========================== */
    suspend fun createTransaksi(request: TransaksiRequest): Result<Transaksi> {
        return try {
            val token = userPreferences.token.first()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("Token tidak ditemukan. Silakan login kembali."))
            }

            ApiClient.setToken(token)

            // Panggil endpoint POST /transaksi
            val response = api.createTransaksi(request)

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception("Gagal membuat transaksi"))
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
     *  UPDATE Transaksi
     *  Mengubah data transaksi berdasarkan ID
     *  =========================== */
    suspend fun updateTransaksi(id: Int, request: TransaksiRequest): Result<Transaksi> {
        return try {
            val token = userPreferences.token.first()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("Token tidak ditemukan. Silakan login kembali."))
            }

            ApiClient.setToken(token)

            // Panggil endpoint PUT /transaksi/{id}
            val response = api.updateTransaksi(id, request)

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception("Gagal mengupdate transaksi"))
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
     *  DELETE Transaksi
     *  Menghapus transaksi berdasarkan ID
     *  =========================== */
    suspend fun deleteTransaksi(id: Int): Result<String> {
        return try {
            val token = userPreferences.token.first()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("Token tidak ditemukan. Silakan login kembali."))
            }

            ApiClient.setToken(token)

            // Panggil endpoint DELETE /transaksi/{id}
            val response = api.deleteTransaksi(id)

            if (response.isSuccessful) {
                val body = response.body()
                Result.success(body?.message ?: "Transaksi berhasil dihapus")
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
     *  Mengambil pesan error dari response body (format JSON)
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
