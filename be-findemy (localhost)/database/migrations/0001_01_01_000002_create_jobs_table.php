<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

// Migration untuk tabel antrian (queue) dan job Laravel
return new class extends Migration
{
    /**
     * Menjalankan migration (membuat tabel)
     */
    public function up(): void
    {
        // Tabel jobs untuk menyimpan antrian job
        Schema::create('jobs', function (Blueprint $table) {
            $table->id(); // Primary key
            $table->string('queue')->index(); // Nama queue
            $table->longText('payload'); // Data job
            $table->unsignedTinyInteger('attempts'); // Jumlah percobaan
            $table->unsignedInteger('reserved_at')->nullable(); // Waktu job diambil worker
            $table->unsignedInteger('available_at'); // Waktu job tersedia
            $table->unsignedInteger('created_at'); // Waktu dibuat
        });

        // Tabel batch job (sekumpulan job)
        Schema::create('job_batches', function (Blueprint $table) {
            $table->string('id')->primary(); // ID batch
            $table->string('name'); // Nama batch
            $table->integer('total_jobs'); // Total job
            $table->integer('pending_jobs'); // Job belum selesai
            $table->integer('failed_jobs'); // Job gagal
            $table->longText('failed_job_ids'); // ID job yang gagal
            $table->mediumText('options')->nullable(); // Opsi batch
            $table->integer('cancelled_at')->nullable(); // Waktu dibatalkan
            $table->integer('created_at'); // Waktu dibuat
            $table->integer('finished_at')->nullable(); // Waktu selesai
        });

        // Tabel job yang gagal dieksekusi
        Schema::create('failed_jobs', function (Blueprint $table) {
            $table->id(); // Primary key
            $table->string('uuid')->unique(); // UUID job
            $table->text('connection'); // Koneksi queue
            $table->text('queue'); // Nama queue
            $table->longText('payload'); // Data job
            $table->longText('exception'); // Pesan error
            $table->timestamp('failed_at')->useCurrent(); // Waktu gagal
        });
    }

    /**
     * Membatalkan migration (menghapus tabel)
     */
    public function down(): void
    {
        Schema::dropIfExists('jobs');
        Schema::dropIfExists('job_batches');
        Schema::dropIfExists('failed_jobs');
    }
};
