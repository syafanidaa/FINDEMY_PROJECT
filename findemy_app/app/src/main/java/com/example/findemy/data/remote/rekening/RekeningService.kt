package com.example.findemy.data.remote.rekening
// Package service API untuk fitur rekening

import com.example.findemy.data.model.MessageResponse
import com.example.findemy.data.model.RekeningListResponse
import com.example.findemy.data.model.RekeningRequest
import com.example.findemy.data.model.RekeningSingleResponse
import retrofit2.Response
import retrofit2.http.*

/**
 * RekeningService
 * Interface Retrofit untuk mengelola data rekening
 */
interface RekeningService {

    // GET /rekening
    // Mengambil semua rekening milik user
    @GET("rekening")
    suspend fun getRekenings(): Response<RekeningListResponse>

    // POST /rekening
    // Menambahkan rekening baru
    @POST("rekening")
    suspend fun createRekening(
        @Body request: RekeningRequest
    ): Response<RekeningSingleResponse>

    // PUT /rekening/{id}
    // Memperbarui rekening berdasarkan ID
    @PUT("rekening/{id}")
    suspend fun updateRekening(
        @Path("id") id: Int,
        @Body request: RekeningRequest
    ): Response<RekeningSingleResponse>

    // DELETE /rekening/{id}
    // Menghapus rekening berdasarkan ID
    @DELETE("rekening/{id}")
    suspend fun deleteRekening(
        @Path("id") id: Int
    ): Response<MessageResponse>
}
