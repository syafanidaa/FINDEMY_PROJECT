<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Menjalankan proses pembuatan tabel event.
     */
    public function up(): void
    {
        Schema::create('event', function (Blueprint $table) {
            $table->id(); // Primary key tabel event
            $table->unsignedBigInteger('user_id'); // Relasi ke tabel users
            $table->string('judul'); // Judul event
            $table->string('tanggal_mulai'); // Tanggal mulai event
            $table->string('tanggal_selesai'); // Tanggal selesai event
            $table->boolean('pasang_pengingat'); // Status pengingat event
            $table->timestamps(); // created_at dan updated_at
        });
    }

    /**
     * Menghapus tabel event jika migration di-rollback.
     */
    public function down(): void
    {
        Schema::dropIfExists('event'); // Menghapus tabel event
    }
};
