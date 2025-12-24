package com.example.findemy.data.remote.event
// Package service API untuk fitur event

import com.example.findemy.data.model.EventListResponse
import com.example.findemy.data.model.EventRequest
import com.example.findemy.data.model.EventSingleResponse
import com.example.findemy.data.model.MessageResponse
import retrofit2.Response
import retrofit2.http.*

/**
 * EventService
 * Interface Retrofit untuk CRUD data event
 */
interface EventService {

    // GET /event
    // Mengambil seluruh event milik user
    @GET("event")
    suspend fun getEvents(): Response<EventListResponse>

    // GET /event/{id}
    // Mengambil detail satu event berdasarkan ID
    @GET("event/{id}")
    suspend fun getEvent(
        @Path("id") id: Int
    ): Response<EventSingleResponse>

    // POST /event
    // Menambahkan event baru
    @POST("event")
    suspend fun createEvent(
        @Body request: EventRequest
    ): Response<EventSingleResponse>

    // PUT /event/{id}
    // Memperbarui event berdasarkan ID
    @PUT("event/{id}")
    suspend fun updateEvent(
        @Path("id") id: Int,
        @Body request: EventRequest
    ): Response<EventSingleResponse>

    // DELETE /event/{id}
    // Menghapus event berdasarkan ID
    @DELETE("event/{id}")
    suspend fun deleteEvent(
        @Path("id") id: Int
    ): Response<MessageResponse>
}
