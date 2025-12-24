package com.example.findemy.data.model
// Package untuk model request rekening

/**
 * Request untuk menambahkan atau memperbarui rekening
 */
data class RekeningRequest(
    val nama: String,   // Nama rekening
    val saldo: Int,     // Saldo awal
)
