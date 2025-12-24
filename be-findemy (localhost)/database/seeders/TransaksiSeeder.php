<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;

class TransaksiSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        DB::table('transaksi')->insert([
            [
                'user_id' => 1,
                'rekening_id' => 1,
                'jenis' => 'pemasukan',
                'keterangan' => 'Gaji',
                'jumlah' => 1000000,
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'user_id' => 1,
                'rekening_id' => 1,
                'jenis' => 'pengeluaran',
                'keterangan' => 'Bayar makan',
                'jumlah' => 15000,
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'user_id' => 1,
                'rekening_id' => 1,
                'jenis' => 'pengeluaran',
                'keterangan' => 'Bayar Listrik',
                'jumlah' => 100000,
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'user_id' => 1,
                'rekening_id' => 2,
                'jenis' => 'pemasukan',
                'keterangan' => 'Freelance',
                'jumlah' => 200000,
                'created_at' => now(),
                'updated_at' => now(),
            ],
        ]);
    }
}
