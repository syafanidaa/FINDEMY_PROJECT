<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Menjalankan proses pembuatan tabel
     */
    public function up(): void
    {
        Schema::create('rekening', function (Blueprint $table) {
            $table->id(); // Primary key tabel rekening
            $table->unsignedBigInteger('user_id'); // Relasi ke tabel users
            $table->string('nama'); // Nama rekening
            $table->bigInteger('saldo'); // Saldo rekening
            $table->timestamps(); // Created_at dan updated_at
            $table->softDeletes(); // Soft delete (deleted_at)
        });
    }

    /**
     * Membatalkan proses migrasi (drop tabel)
     */
    public function down(): void
    {
        Schema::dropIfExists('rekening'); // Menghapus tabel rekening
    }
};
