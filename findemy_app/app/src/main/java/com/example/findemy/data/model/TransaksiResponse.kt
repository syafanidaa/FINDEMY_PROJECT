package com.example.findemy.data.model
// Package untuk model data transaksi

/**
 * Response API untuk daftar transaksi (rekap)
 */
data class TransaksiListResponse(
    val success: Boolean,        // Status request
    val message: String,         // Pesan server
    val data: RekapTransaksi     // Data rekap transaksi
)

/**
 * Response API untuk satu transaksi
 */
data class TransaksiSingleResponse(
    val success: Boolean,        // Status request
    val message: String,         // Pesan server
    val data: Transaksi          // Data transaksi
)

/**
 * Rekap transaksi per bulan
 */
data class RekapTransaksi(
    val bulan: String,           // Bulan
    val tahun: String,           // Tahun
    val saldo: String,           // Saldo akhir
    val pemasukan: String,       // Total pemasukan
    val pengeluaran: String,     // Total pengeluaran
    val selisih: String,         // Selisih pemasukan & pengeluaran
    val transaksi: List<Transaksi> // Daftar transaksi
)

/**
 * Model data transaksi
 */
data class Transaksi(
    val id: Int,                 // ID transaksi
    val user_id: Int,            // ID user
    val rekening_id: Int,        // ID rekening
    val jenis: String,           // Jenis transaksi
    val keterangan: String,      // Keterangan
    val jumlah: Int,             // Nominal
    val rekening: Rekening,      // Data rekening
    val created_at: String,      // Waktu dibuat
    val updated_at: String       // Waktu diperbarui
)
