package com.example.findemy.data.repository

// Context digunakan untuk mengakses DataStore (UserPreferences)
import android.content.Context

// DataStore untuk menyimpan dan mengambil token user
import com.example.findemy.data.local.UserPreferences

// Model data event
import com.example.findemy.data.model.Event
import com.example.findemy.data.model.EventListResponse
import com.example.findemy.data.model.EventRequest

// ApiClient dan service event
import com.example.findemy.data.remote.ApiClient
import com.example.findemy.data.remote.event.EventService

// Coroutine Flow
import kotlinx.coroutines.flow.first

// Untuk parsing error response JSON
import org.json.JSONObject

// Exception dari Retrofit dan jaringan
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

// Repository untuk mengelola data Event (API + token)
class EventRepository(private val context: Context) {

    // Inisialisasi EventService dari Retrofit
    private val api = ApiClient.retrofit.create(EventService::class.java)

    // UserPreferences untuk mengambil token login
    private val userPreferences = UserPreferences(context)

    /* ===========================
       GET Event
       Mengambil semua data event
       =========================== */
    suspend fun getEvents(): Result<EventListResponse> {
        return try {
            // Ambil token dari DataStore
            val token = userPreferences.token.first()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("Token tidak ditemukan. Silakan login kembali."))
            }

            // Set token ke ApiClient
            ApiClient.setToken(token)

            // Panggil API get events
            val response = api.getEvents()

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Data tidak ditemukan"))
            } else {
                Result.failure(Exception(parseErrorMessage(response)))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpException(e)))
        } catch (e: IOException) {
            Result.failure(Exception("Tidak dapat terhubung ke server"))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Terjadi kesalahan"))
        }
    }

    /* ===========================
       CREATE Event
       Menambahkan event baru
       =========================== */
    suspend fun createEvent(request: EventRequest): Result<Event> {
        return try {
            val token = userPreferences.token.first()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("Token tidak ditemukan. Silakan login kembali."))
            }

            ApiClient.setToken(token)
            val response = api.createEvent(request)

            if (response.isSuccessful) {
                response.body()?.data?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Gagal membuat event"))
            } else {
                Result.failure(Exception(parseErrorMessage(response)))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpException(e)))
        } catch (e: IOException) {
            Result.failure(Exception("Tidak dapat terhubung ke server"))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Terjadi kesalahan"))
        }
    }

    /* ===========================
       UPDATE Event
       Mengubah data event
       =========================== */
    suspend fun updateJadwal(id: Int, request: EventRequest): Result<Event> {
        return try {
            val token = userPreferences.token.first()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("Token tidak ditemukan. Silakan login kembali."))
            }

            ApiClient.setToken(token)
            val response = api.updateEvent(id, request)

            if (response.isSuccessful) {
                response.body()?.data?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Gagal mengupdate event"))
            } else {
                Result.failure(Exception(parseErrorMessage(response)))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpException(e)))
        } catch (e: IOException) {
            Result.failure(Exception("Tidak dapat terhubung ke server"))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Terjadi kesalahan"))
        }
    }

    /* ===========================
       DELETE Event
       Menghapus event
       =========================== */
    suspend fun deleteEvent(id: Int): Result<String> {
        return try {
            val token = userPreferences.token.first()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("Token tidak ditemukan. Silakan login kembali."))
            }

            ApiClient.setToken(token)
            val response = api.deleteEvent(id)

            if (response.isSuccessful) {
                Result.success(response.body()?.message ?: "Event berhasil dihapus")
            } else {
                Result.failure(Exception(parseErrorMessage(response)))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpException(e)))
        } catch (e: IOException) {
            Result.failure(Exception("Tidak dapat terhubung ke server"))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Terjadi kesalahan"))
        }
    }

    /* ===========================
       Helper
       Parsing pesan error dari response API
       =========================== */
    private fun parseErrorMessage(response: Response<*>): String {
        return try {
            val errorBody = response.errorBody()?.string()
            JSONObject(errorBody ?: "{}")
                .optString("message", "Terjadi kesalahan pada server")
        } catch (e: Exception) {
            "Terjadi kesalahan pada server"
        }
    }

    /* ===========================
       Helper
       Parsing error dari HttpException
       =========================== */
    private fun parseHttpException(e: HttpException): String {
        return try {
            val errorBody = e.response()?.errorBody()?.string()
            JSONObject(errorBody ?: "{}")
                .optString("message", "Terjadi kesalahan pada server")
        } catch (ex: Exception) {
            "Terjadi kesalahan pada server"
        }
    }
}
