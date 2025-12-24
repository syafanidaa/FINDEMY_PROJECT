<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Menjalankan migration (membuat tabel jadwal)
     */
    public function up(): void
    {
        Schema::create('jadwal', function (Blueprint $table) {
            $table->id(); // Primary key
            $table->unsignedBigInteger('user_id'); // Relasi ke user
            $table->string('mata_kuliah'); // Nama mata kuliah
            $table->string('dosen'); // Nama dosen
            $table->string('ruangan'); // Ruangan kelas
            $table->string('hari'); // Hari perkuliahan
            $table->string('jam_mulai'); // Jam mulai
            $table->string('jam_selesai'); // Jam selesai
            $table->boolean('pasang_pengingat'); // Status pengingat
            $table->timestamps(); // Created_at & updated_at
        });
    }

    /**
     * Menghapus tabel jadwal
     */
    public function down(): void
    {
        Schema::dropIfExists('jadwal');
    }
};
