<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Menjalankan proses pembuatan tabel tugas.
     */
    public function up(): void
    {
        Schema::create('tugas', function (Blueprint $table) {
            $table->id(); // Primary key tabel tugas
            $table->unsignedBigInteger('user_id'); // ID pengguna pemilik tugas
            $table->unsignedBigInteger('jadwal_id'); // Relasi ke tabel jadwal
            $table->string('judul'); // Judul tugas
            $table->string('deskripsi'); // Deskripsi singkat tugas
            $table->string('deadline'); // Batas waktu pengumpulan tugas
            $table->enum('status', ['selesai', 'belum selesai']); // Status pengerjaan tugas
            $table->boolean('pasang_pengingat'); // Penanda pengingat tugas aktif atau tidak
            $table->timestamps(); // Waktu dibuat dan diperbarui
        });
    }

    /**
     * Menghapus tabel tugas saat rollback migration.
     */
    public function down(): void
    {
        Schema::dropIfExists('tugas');
    }
};
