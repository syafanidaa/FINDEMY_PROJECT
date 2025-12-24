package com.example.findemy.data.model
// Package untuk model request tugas

/**
 * Request untuk menambahkan atau memperbarui tugas
 */
data class TugasRequest(
    val jadwal_id: Int,          // ID jadwal terkait
    val judul: String,           // Judul tugas
    val deskripsi: String,       // Deskripsi tugas
    val deadline: String,        // Batas waktu
    val status: String,          // Status tugas
    val pasang_pengingat: Boolean, // Status pengingat
)
