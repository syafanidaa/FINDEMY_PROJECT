<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

/**
 * Model Event
 * Digunakan untuk mengelola data event pada tabel event
 */
class Event extends Model
{
    // Menentukan nama tabel yang digunakan oleh model
    protected $table = 'event';

    // Field yang tidak boleh diisi secara mass assignment
    protected $guarded = ['id'];

    // Mengubah tipe data pasang_pengingat menjadi boolean
    protected $casts = [
        'pasang_pengingat' => 'boolean'
    ];
}
