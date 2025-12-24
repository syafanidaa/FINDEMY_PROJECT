<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Menjalankan migration (membuat tabel)
     */
    public function up(): void
    {
        // Tabel users (data pengguna)
        Schema::create('users', function (Blueprint $table) {
            $table->id(); // Primary key
            $table->string('name'); // Nama user
            $table->string('username'); // Username
            $table->string('email')->unique(); // Email unik
            $table->timestamp('email_verified_at')->useCurrent(); // Waktu verifikasi email
            $table->string('password'); // Password user
            $table->rememberToken(); // Token remember me
            $table->timestamps(); // created_at & updated_at
        });

        // Tabel token reset password
        Schema::create('password_reset_tokens', function (Blueprint $table) {
            $table->string('email')->primary(); // Email sebagai primary key
            $table->string('token'); // Token reset password
            $table->timestamp('created_at')->nullable(); // Waktu pembuatan token
        });

        // Tabel session login
        Schema::create('sessions', function (Blueprint $table) {
            $table->string('id')->primary(); // ID session
            $table->foreignId('user_id')->nullable()->index(); // Relasi ke user
            $table->string('ip_address', 45)->nullable(); // Alamat IP
            $table->text('user_agent')->nullable(); // Informasi browser/device
            $table->longText('payload'); // Data session
            $table->integer('last_activity')->index(); // Waktu aktivitas terakhir
        });
    }

    /**
     * Membatalkan migration (hapus tabel)
     */
    public function down(): void
    {
        Schema::dropIfExists('users');
        Schema::dropIfExists('password_reset_tokens');
        Schema::dropIfExists('sessions');
    }
};
