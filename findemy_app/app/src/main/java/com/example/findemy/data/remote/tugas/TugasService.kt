package com.example.findemy.data.remote.tugas
// Package service API untuk fitur tugas

import com.example.findemy.data.model.MessageResponse
import com.example.findemy.data.model.TugasListResponse
import com.example.findemy.data.model.TugasRequest
import com.example.findemy.data.model.TugasSingleResponse
import retrofit2.Response
import retrofit2.http.*

/**
 * TugasService
 * Interface Retrofit untuk mengelola data tugas
 */
interface TugasService {

    // GET /tugas
    // Mengambil daftar tugas
    // Bisa difilter berdasarkan status tugas
    @GET("tugas")
    suspend fun getTugass(
        @Query("status") status: String? = null
    ): Response<TugasListResponse>

    // GET /tugas/{id}
    // Mengambil detail tugas berdasarkan ID
    @GET("tugas/{id}")
    suspend fun getTugas(
        @Path("id") id: Int
    ): Response<TugasSingleResponse>

    // POST /tugas
    // Menambahkan tugas baru
    @POST("tugas")
    suspend fun createTugas(
        @Body request: TugasRequest
    ): Response<TugasSingleResponse>

    // PUT /tugas/{id}
    // Memperbarui tugas berdasarkan ID
    @PUT("tugas/{id}")
    suspend fun updateTugas(
        @Path("id") id: Int,
        @Body request: TugasRequest
    ): Response<TugasSingleResponse>

    // DELETE /tugas/{id}
    // Menghapus tugas berdasarkan ID
    @DELETE("tugas/{id}")
    suspend fun deleteTugas(
        @Path("id") id: Int
    ): Response<MessageResponse>
}
