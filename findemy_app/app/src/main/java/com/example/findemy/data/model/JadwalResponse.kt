package com.example.findemy.data.model
// Package untuk model data jadwal kuliah

/**
 * Response API untuk daftar jadwal
 */
data class JadwalListResponse(
    val success: Boolean,        // Status request
    val message: String,         // Pesan server
    val data: List<Jadwal>       // List jadwal
)

/**
 * Response API untuk detail satu jadwal
 */
data class JadwalSingleResponse(
    val success: Boolean,        // Status request
    val message: String,         // Pesan server
    val data: Jadwal             // Data jadwal
)

/**
 * Model data jadwal kuliah
 */
data class Jadwal(
    val id: Int,                 // ID jadwal
    val user_id: Int,            // ID pemilik jadwal
    val mata_kuliah: String,     // Mata kuliah
    val dosen: String,           // Dosen
    val ruangan: String,         // Ruangan
    val hari: String,            // Hari
    val jam_mulai: String,       // Jam mulai
    val jam_selesai: String,     // Jam selesai
    val pasang_pengingat: Boolean, // Status pengingat
    val created_at: String,      // Waktu dibuat
    val updated_at: String       // Waktu diperbarui
)
