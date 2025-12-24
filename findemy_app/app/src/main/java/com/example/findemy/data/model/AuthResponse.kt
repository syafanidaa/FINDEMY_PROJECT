package com.example.findemy.data.model
// Package ini berisi model data (data class)
// Model digunakan untuk merepresentasikan struktur data dari API

/**
 * Data class AuthResponse
 * -----------------------
 * Merepresentasikan response dari API autentikasi
 * seperti login dan register
 */
data class AuthResponse(

    // Pesan dari server (informasi sukses atau error)
    val message: String?,

    // Token autentikasi user
    // Digunakan untuk akses API yang membutuhkan otorisasi
    val token: String?,

    // Data user yang berhasil login atau register
    // Bisa bernilai null jika autentikasi gagal
    val user: UserData?
)

/**
 * Data class UserData
 * ------------------
 * Menyimpan informasi dasar user
 */
data class UserData(

    // ID unik user
    val id: Int,

    // Nama user
    val name: String,

    // Email user
    val email: String
)
