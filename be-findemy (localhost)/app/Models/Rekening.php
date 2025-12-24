<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;

/**
 * Model Rekening
 * Merepresentasikan rekening keuangan milik user
 */
class Rekening extends Model
{
    // Mengaktifkan fitur soft delete (data tidak langsung terhapus)
    use SoftDeletes;

    // Tabel database yang digunakan
    protected $table = 'rekening';

    // Kolom yang tidak boleh diisi secara mass assignment
    protected $guarded = ['id'];
}
