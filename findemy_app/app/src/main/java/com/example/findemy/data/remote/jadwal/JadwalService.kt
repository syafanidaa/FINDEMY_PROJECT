package com.example.findemy.data.remote.jadwal
// Package service API untuk fitur jadwal kuliah

import com.example.findemy.data.model.JadwalRequest
import com.example.findemy.data.model.JadwalListResponse
import com.example.findemy.data.model.JadwalSingleResponse
import com.example.findemy.data.model.MessageResponse
import retrofit2.Response
import retrofit2.http.*

/**
 * JadwalService
 * Interface Retrofit untuk mengelola jadwal kuliah
 */
interface JadwalService {

    // GET /jadwal
    // Mengambil daftar jadwal
    // Parameter "hari" bersifat opsional untuk filter jadwal
    @GET("jadwal")
    suspend fun getJadwals(
        @Query("hari") hari: String? = null
    ): Response<JadwalListResponse>

    // GET /jadwal/{id}
    // Mengambil detail jadwal berdasarkan ID
    @GET("jadwal/{id}")
    suspend fun getJadwal(
        @Path("id") id: Int
    ): Response<JadwalSingleResponse>

    // POST /jadwal
    // Menambahkan jadwal baru
    @POST("jadwal")
    suspend fun createJadwal(
        @Body request: JadwalRequest
    ): Response<JadwalSingleResponse>

    // PUT /jadwal/{id}
    // Memperbarui jadwal berdasarkan ID
    @PUT("jadwal/{id}")
    suspend fun updateJadwal(
        @Path("id") id: Int,
        @Body request: JadwalRequest
    ): Response<JadwalSingleResponse>

    // DELETE /jadwal/{id}
    // Menghapus jadwal berdasarkan ID
    @DELETE("jadwal/{id}")
    suspend fun deleteJadwal(
        @Path("id") id: Int
    ): Response<MessageResponse>
}
