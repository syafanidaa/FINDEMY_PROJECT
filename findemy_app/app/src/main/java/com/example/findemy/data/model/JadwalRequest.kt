```kotlin
package com.example.findemy.data.model
// Package untuk model request jadwal kuliah

/**
 * Request untuk menambahkan jadwal kuliah
 */
data class JadwalRequest(
    val mata_kuliah: String,     // Nama mata kuliah
    val dosen: String,           // Nama dosen
    val ruangan: String,         // Ruangan kuliah
    val hari: String,            // Hari kuliah
    val jam_mulai: String,       // Jam mulai
    val jam_selesai: String,     // Jam selesai
    val pasang_pengingat: Boolean, // Status pengingat
)
```
