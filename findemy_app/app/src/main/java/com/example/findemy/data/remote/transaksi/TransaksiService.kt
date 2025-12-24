package com.example.findemy.data.remote.transaksi
// Package service API untuk fitur transaksi keuangan

import com.example.findemy.data.model.MessageResponse
import com.example.findemy.data.model.TransaksiListResponse
import com.example.findemy.data.model.TransaksiRequest
import com.example.findemy.data.model.TransaksiSingleResponse
import retrofit2.Response
import retrofit2.http.*

/**
 * TransaksiService
 * Interface Retrofit untuk mengelola transaksi keuangan
 */
interface TransaksiService {

    // GET /transaksi
    // Mengambil daftar transaksi
    // Bisa difilter berdasarkan bulan dan tahun
    @GET("transaksi")
    suspend fun getTransaksis(
        @Query("bulan") bulan: String? = null,   // Filter bulan (opsional)
        @Query("tahun") tahun: String? = null    // Filter tahun (opsional)
    ): Response<TransaksiListResponse>

    // POST /transaksi
    // Menambahkan transaksi baru
    @POST("transaksi")
    suspend fun createTransaksi(
        @Body request: TransaksiRequest
    ): Response<TransaksiSingleResponse>

    // PUT /rekening/{id}
    // Memperbarui data transaksi berdasarkan ID
    @PUT("rekening/{id}")
    suspend fun updateTransaksi(
        @Path("id") id: Int,
        @Body request: TransaksiRequest
    ): Response<TransaksiSingleResponse>

    // DELETE /rekening/{id}
    // Menghapus transaksi berdasarkan ID
    @DELETE("rekening/{id}")
    suspend fun deleteTransaksi(
        @Path("id") id: Int
    ): Response<MessageResponse>
}
