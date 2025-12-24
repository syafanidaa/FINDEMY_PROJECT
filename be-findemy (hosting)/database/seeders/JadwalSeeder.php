<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;

class JadwalSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        DB::table('jadwal')->insert([
            [
                'user_id' => 1,
                'mata_kuliah' => 'Matematika Diskrit',
                'dosen' => 'Dr. Agus',
                'ruangan' => 'A101',
                'hari' => 'Senin',
                'jam_mulai' => '08:00',
                'jam_selesai' => '10:00',
                'pasang_pengingat' => true,
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'user_id' => 1,
                'mata_kuliah' => 'Pemrograman Web',
                'dosen' => 'Ibu Siti',
                'ruangan' => 'B202',
                'hari' => 'Selasa',
                'jam_mulai' => '10:00',
                'jam_selesai' => '12:00',
                'pasang_pengingat' => true,
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'user_id' => 1,
                'mata_kuliah' => 'Tugas Basis Data',
                'dosen' => 'Bapak Joko',
                'ruangan' => 'C303',
                'hari' => 'Rabu',
                'jam_mulai' => '13:00',
                'jam_selesai' => '15:00',
                'pasang_pengingat' => true,
                'created_at' => now(),
                'updated_at' => now(),
            ],
        ]);
    }
}
