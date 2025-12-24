<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

// Migration anonymous class untuk membuat tabel 'transaksi'
return new class extends Migration
{
    /**
     * Run the migrations.
     * Method ini dipanggil saat menjalankan `php artisan migrate`
     */
    public function up(): void
    {
        // Membuat tabel 'transaksi'
        Schema::create('transaksi', function (Blueprint $table) {
            $table->id(); // Kolom primary key 'id' auto increment
            $table->unsignedBigInteger('user_id'); // Kolom untuk relasi ke tabel 'users', tipe unsigned big integer
            $table->unsignedBigInteger('rekening_id'); // Kolom untuk relasi ke tabel 'rekening', tipe unsigned big integer
            $table->enum('jenis', ['pemasukan', 'pengeluaran']); // Kolom untuk tipe transaksi, hanya bisa 'pemasukan' atau 'pengeluaran'
            $table->string('keterangan'); // Kolom untuk keterangan transaksi, tipe string
            $table->integer('jumlah'); // Kolom untuk jumlah uang, tipe integer
            $table->timestamps(); // Kolom 'created_at' dan 'updated_at' otomatis
        });
    }

    /**
     * Reverse the migrations.
     * Method ini dipanggil saat menjalankan `php artisan migrate:rollback`
     */
    public function down(): void
    {
        Schema::dropIfExists('transaksi'); // Menghapus tabel 'transaksi' jika rollback
    }
};
