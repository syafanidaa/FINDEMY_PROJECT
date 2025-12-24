package com.example.findemy.data.model
// Package untuk model response umum

/**
 * Response sederhana dari API
 */
data class MessageResponse(
    val success: Boolean,   // Status request
    val message: String    // Pesan dari server
)
