package com.example.findemy.data.remote

// Interceptor digunakan untuk memodifikasi request sebelum dikirim ke server
import okhttp3.Interceptor

// OkHttpClient adalah HTTP client yang digunakan oleh Retrofit
import okhttp3.OkHttpClient

// Library untuk menampilkan log request & response API
import okhttp3.logging.HttpLoggingInterceptor

// Retrofit digunakan untuk komunikasi REST API
import retrofit2.Retrofit

// GsonConverterFactory digunakan untuk mengubah JSON ↔ object Kotlin
import retrofit2.converter.gson.GsonConverterFactory

// ApiClient sebagai pusat konfigurasi Retrofit dan OkHttp
object ApiClient {

    // Base URL dari backend API Findemy
    private const val BASE_URL = "https://findemy.xyz/api/"

    // Menyimpan token autentikasi (Bearer Token)
    // Akan bernilai null jika user belum login
    private var token: String? = null

    // Fungsi untuk menyimpan atau memperbarui token setelah login
    fun setToken(newToken: String?) {
        token = newToken
    }

    // Logging interceptor untuk menampilkan detail request & response API
    // Level.BODY menampilkan header dan body (cocok untuk debugging)
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Interceptor untuk menambahkan header ke setiap request API
    private val authInterceptor = Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()
            // Memberitahu server bahwa response yang diharapkan adalah JSON
            .addHeader("Accept", "application/json")

        // Jika token tersedia, tambahkan Authorization Bearer
        token?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        // Mengirim request ke server
        chain.proceed(requestBuilder.build())
    }

    // Konfigurasi OkHttpClient
    // Digunakan oleh Retrofit untuk mengirim request HTTP
    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor) // Menambahkan header otomatis
        .addInterceptor(logging)         // Menampilkan log API
        .build()

    // Instance Retrofit utama
    // Digunakan untuk membuat service API (AuthService, JadwalService, dll)
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL) // URL utama API
        .addConverterFactory(GsonConverterFactory.create()) // Konversi JSON ↔ Kotlin
        .client(client) // OkHttpClient yang sudah dikonfigurasi
        .build()
}
