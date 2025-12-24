<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

/**
 * Model Transaksi
 * Menangani data pemasukan dan pengeluaran user
 */
class Transaksi extends Model
{
    // Menentukan tabel yang dipakai oleh model
    protected $table = 'transaksi';

    // Melindungi kolom id dari mass assignment
    protected $guarded = ['id'];

    // Relasi ke tabel rekening (termasuk yang sudah di-soft delete)
    public function rekening()
    {
        return $this->belongsTo(Rekening::class, 'rekening_id', 'id')->withTrashed();
    }
}
