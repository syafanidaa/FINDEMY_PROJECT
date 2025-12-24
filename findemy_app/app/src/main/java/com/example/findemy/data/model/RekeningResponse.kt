package com.example.findemy.data.model
// Package untuk model data rekening

/**
 * Response API untuk daftar rekening
 */
data class RekeningListResponse(
    val success: Boolean,        // Status request
    val message: String,         // Pesan server
    val data: List<Rekening>     // List rekening
)

/**
 * Response API untuk detail satu rekening
 */
data class RekeningSingleResponse(
    val success: Boolean,        // Status request
    val message: String,         // Pesan server
    val data: Rekening           // Data rekening
)

/**
 * Model data rekening
 */
data class Rekening(
    val id: Int,                 // ID rekening
    val user_id: Int,            // ID pemilik rekening
    val nama: String,            // Nama rekening
    val saldo: Int,              // Saldo
    val created_at: String,      // Waktu dibuat
    val updated_at: String       // Waktu diperbarui
)
