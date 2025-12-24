package com.example.findemy.data.model
// Package untuk semua model data event

/**
 * Response API untuk mengambil daftar event milik user
 */
data class EventListResponse(
    val success: Boolean,   // Status request (berhasil / gagal)
    val message: String,    // Pesan dari server
    val data: List<Event>   // List data event
)

/**
 * Response API untuk mengambil detail satu event
 */
data class EventSingleResponse(
    val success: Boolean,   // Status request
    val message: String,    // Pesan server
    val data: Event         // Data satu event
)

/**
 * Model data utama event
 */
data class Event(
    val id: Int,                // ID event
    val user_id: Int,           // ID pemilik event (user)
    val judul: String,          // Judul event
    val tanggal_mulai: String,  // Tanggal mulai
    val tanggal_selesai: String,// Tanggal selesai
    val pasang_pengingat: Boolean, // Status pengingat
    val created_at: String,     // Waktu dibuat
    val updated_at: String      // Waktu terakhir diubah
)
