<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

/**
 * Model Jadwal
 * Menyimpan dan mengelola data jadwal perkuliahan user
 */
class Jadwal extends Model
{
    // Nama tabel di database
    protected $table = 'jadwal';

    // Kolom yang dilindungi dari pengisian massal
    protected $guarded = ['id'];

    // Casting tipe data agar pasang_pengingat bernilai true/false
    protected $casts = [
        'pasang_pengingat' => 'boolean'
    ];
}
