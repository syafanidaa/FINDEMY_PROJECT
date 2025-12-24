<?php

namespace Database\Factories;

use Illuminate\Database\Eloquent\Factories\Factory;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Str;

/**
 * Factory untuk membuat data dummy User
 */
class UserFactory extends Factory
{
    /**
     * Menyimpan password default agar tidak di-hash berulang
     */
    protected static ?string $password;

    /**
     * Data default User saat factory dijalankan
     */
    public function definition(): array
    {
        return [
            // Nama user acak
            'name' => fake()->name(),

            // Email unik
            'email' => fake()->unique()->safeEmail(),

            // Email dianggap sudah terverifikasi
            'email_verified_at' => now(),

            // Password default (password)
            'password' => static::$password ??= Hash::make('password'),

            // Token untuk fitur remember me
            'remember_token' => Str::random(10),
        ];
    }

    /**
     * State user dengan email belum terverifikasi
     */
    public function unverified(): static
    {
        return $this->state(fn (array $attributes) => [
            'email_verified_at' => null,
        ]);
    }
}
