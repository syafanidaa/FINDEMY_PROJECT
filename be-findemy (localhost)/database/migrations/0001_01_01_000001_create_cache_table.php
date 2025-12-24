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
        // Tabel utama user
        Schema::create('users', function (Blueprint $table) {
            $table->id(); // Primary key
            $table->string('name'); // Nama user
            $table->string('username'); // Username user
            $table->string('email')->unique(); // Email unik
            $table->timestamp('email_verified_at')->useCurrent(); // Waktu verifikasi email
            $table->string('password'); // Password terenkripsi
            $table->rememberToken(); // Token remember me
            $table->timestamps(); // created_at & updated_at
        });

        // Tabel reset password
        Schema::create('password_reset_tokens', function (Blueprint $table) {
            $table->string('email')->primary(); // Email user
            $table->string('token'); // Token reset password
            $table->timestamp('created_at')->nullable(); // Waktu pembuatan token
        });

        // Tabel penyimpanan session
        Schema::create('sessions', function (Blueprint $table) {
            $table->string('id')->primary(); // ID session
            $table->foreignId('user_id')->nullable()->index(); // Relasi ke user
            $table->string('ip_address', 45)->nullable(); // IP address
            $table->text('user_agent')->nullable(); // Informasi browser
            $table->longText('payload'); // Data session
            $table->integer('last_activity')->index(); // Aktivitas terakhir
        });
    }

    /**
     * Menghapus tabel saat rollback
     */
    public function down(): void
    {
        Schema::dropIfExists('users');
        Schema::dropIfExists('password_reset_tokens');
        Schema::dropIfExists('sessions');
    }
};
