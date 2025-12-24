<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Menjalankan proses pembuatan tabel.
     */
    public function up(): void
    {
        // Membuat tabel personal_access_tokens (digunakan oleh Laravel Sanctum)
        Schema::create('personal_access_tokens', function (Blueprint $table) {
            $table->id(); // Primary key
            $table->morphs('tokenable'); // Relasi polymorphic ke user atau model lain
            $table->text('name'); // Nama token
            $table->string('token', 64)->unique(); // Token unik
            $table->text('abilities')->nullable(); // Hak akses token
            $table->timestamp('last_used_at')->nullable(); // Waktu terakhir token digunakan
            $table->timestamp('expires_at')->nullable()->index(); // Waktu kedaluwarsa token
            $table->timestamps(); // created_at dan updated_at
        });
    }

    /**
     * Menghapus tabel saat rollback migration.
     */
    public function down(): void
    {
        Schema::dropIfExists('personal_access_tokens');
    }
};
