package com.example.findemy.data.model
// Package untuk data model aplikasi

/**
 * Model request untuk membuat event
 */
data class EventRequest(

    // Judul event
    val judul: String,

    // Tanggal mulai event
    val tanggal_mulai: String,

    // Tanggal selesai event
    val tanggal_selesai: String,

    // Status pengingat event
    val pasang_pengingat: Boolean,
)
