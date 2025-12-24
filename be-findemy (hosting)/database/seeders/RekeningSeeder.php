<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;

class RekeningSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        DB::table('rekening')->insert([
            [
                'user_id' => 1,
                'nama' => 'BCA',
                'saldo' => 885000,
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'user_id' => 1,
                'nama' => 'BRI',
                'saldo' => 200000,
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'user_id' => 1,
                'nama' => 'DANA',
                'saldo' => 0,
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'user_id' => 1,
                'nama' => 'Gopay',
                'saldo' => 0,
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'user_id' => 1,
                'nama' => 'OVO',
                'saldo' => 0,
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'user_id' => 1,
                'nama' => 'Uang Tunai',
                'saldo' => 0,
                'created_at' => now(),
                'updated_at' => now(),
            ],
        ]);
    }
}
