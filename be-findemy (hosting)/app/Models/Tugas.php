<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

/**
 * Model Tugas
 * Menyimpan data tugas akademik milik user
 */
class Tugas extends Model
{
    // Nama tabel yang digunakan di database
    protected $table = 'tugas';

    // Kolom id tidak bisa diisi secara massal
    protected $guarded = ['id'];

    // Konversi nilai pengingat ke tipe boolean
    protected $casts = [
        'pasang_pengingat' => 'boolean'
    ];

    // Relasi ke jadwal mata kuliah
    public function jadwal()
    {
        return $this->belongsTo(Jadwal::class, 'jadwal_id', 'id');
    }
}
