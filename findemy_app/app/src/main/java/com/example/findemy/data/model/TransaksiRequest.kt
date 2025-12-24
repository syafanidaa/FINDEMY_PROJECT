package com.example.findemy.data.model
// Package untuk model request transaksi

/**
 * Request untuk menambahkan transaksi
 */
data class TransaksiRequest(
    val rekening_id: Int,    // ID rekening
    val jenis: String,       // Jenis transaksi (masuk / keluar)
    val keterangan: String,  // Keterangan transaksi
    val jumlah: Int,         // Nominal transaksi
)
