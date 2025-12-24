<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;

class TugasSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        DB::table('tugas')->insert([
            [
                'user_id' => 1,
                'jadwal_id' => 1,
                'judul' => 'Tugas Matematika Diskrit',
                'deskripsi' => 'Mengerjakan soal kombinatorika dan logika proposisional.',
                'deadline' => '2025-11-05 23:59',
                'status' => 'selesai',
                'pasang_pengingat' => true,
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'user_id' => 1,
                'jadwal_id' => 2,
                'judul' => 'Tugas Pemrograman Web',
                'deskripsi' => 'Membuat aplikasi CRUD menggunakan Laravel.',
                'deadline' => '2025-11-07 23:59',
                'status' => 'belum selesai',
                'pasang_pengingat' => true,
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'user_id' => 1,
                'jadwal_id' => 3,
                'judul' => 'Tugas Basis Data',
                'deskripsi' => 'Mendesain ERD dan membuat query SQL untuk studi kasus.',
                'deadline' => '2025-11-10 23:59',
                'status' => 'belum selesai',
                'pasang_pengingat' => true,
                'created_at' => now(),
                'updated_at' => now(),
            ],
        ]);
    }
}
