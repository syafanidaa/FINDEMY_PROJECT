<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;

class EventSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        DB::table('event')->insert([
            [
                'user_id' => 1,
                'judul' => 'Workshop Laravel',
                'tanggal_mulai' => '2025-11-01 09:00:00',
                'tanggal_selesai' => '2025-11-01 17:00:00',
                'pasang_pengingat' => true,
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'user_id' => 1,
                'judul' => 'Seminar AI',
                'tanggal_mulai' => '2025-11-05 08:00:00',
                'tanggal_selesai' => '2025-11-05 16:00:00',
                'pasang_pengingat' => true,
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'user_id' => 1,
                'judul' => 'Pelatihan Python',
                'tanggal_mulai' => '2025-11-10 08:00:00',
                'tanggal_selesai' => '2025-11-12 16:00:00',
                'pasang_pengingat' => true,
                'created_at' => now(),
                'updated_at' => now(),
            ],
        ]);
    }
}
