<?php

namespace App\Models;

// use Illuminate\Contracts\Auth\MustVerifyEmail;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Foundation\Auth\User as Authenticatable;
use Illuminate\Notifications\Notifiable;
use Laravel\Sanctum\HasApiTokens;

/**
 * Model User
 * Digunakan untuk autentikasi dan data akun pengguna
 */
class User extends Authenticatable
{
    /**
     * Trait bawaan Laravel:
     * - HasFactory: untuk factory
     * - Notifiable: untuk notifikasi
     * - HasApiTokens: untuk autentikasi API (Sanctum)
     */
    use HasFactory, Notifiable, HasApiTokens;

    /**
     * Atribut yang boleh diisi secara mass assignment
     */
    protected $fillable = [
        'name',
        'username',
        'email',
        'password',
    ];

    /**
     * Atribut yang disembunyikan saat data di-serialize (JSON)
     */
    protected $hidden = [
        'password',
        'remember_token',
    ];

    /**
     * Casting tipe data atribut tertentu
     */
    protected function casts(): array
    {
        return [
            'email_verified_at' => 'datetime',
            'password' => 'hashed',
        ];
    }
}
