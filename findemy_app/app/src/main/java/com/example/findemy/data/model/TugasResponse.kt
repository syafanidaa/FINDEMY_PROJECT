package com.example.findemy.data.model
// Package untuk model data tugas

/**
 * Response API untuk daftar tugas
 */
data class TugasListResponse(
    val success: Boolean,        // Status request
    val message: String,         // Pesan server
    val data: List<Tugas>        // List tugas
)

/**
 * Response API untuk detail satu tugas
 */
data class TugasSingleResponse(
    val success: Boolean,        // Status request
    val message: String,         // Pesan server
    val data: Tugas              // Data tugas
)

/**
 * Model data tugas
 */
data class Tugas(
    val id: Int,                 // ID tugas
    val user_id: Int,            // ID user
    val jadwal: Jadwal,          // Jadwal terkait
    val judul: String,           // Judul tugas
    val deskripsi: String,       // Deskripsi
    val deadline: String,        // Deadline
    val status: String,          // Status tugas
    val pasang_pengingat: Boolean, // Status pengingat
    val created_at: String,      // Waktu dibuat
    val updated_at: String       // Waktu diperbarui
)
