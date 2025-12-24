<?php

namespace App\Providers;

use Illuminate\Support\ServiceProvider;

/**
 * AppServiceProvider
 * Provider utama untuk mendaftarkan dan menjalankan layanan aplikasi
 */
class AppServiceProvider extends ServiceProvider
{
    /**
     * Register service atau binding ke container
     * Biasanya untuk dependency injection atau konfigurasi global
     */
    public function register(): void
    {
        //
    }

    /**
     * Method yang dijalankan saat aplikasi selesai booting
     * Umumnya digunakan untuk konfigurasi runtime
     */
    public function boot(): void
    {
        //
    }
}
