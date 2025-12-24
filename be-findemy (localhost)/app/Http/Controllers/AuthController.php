<?php

namespace App\Http\Controllers;

// Import controller utama Laravel
use App\Http\Controllers\Controller;

// Import model User untuk interaksi tabel users
use App\Models\User;

// Import Request untuk menangkap data dari client
use Illuminate\Http\Request;

// Import Hash untuk enkripsi password
use Illuminate\Support\Facades\Hash;

// Digunakan untuk error validasi login
use Illuminate\Validation\ValidationException;

// Facade DB untuk query langsung ke database
use Illuminate\Support\Facades\DB;

// Facade Mail untuk pengiriman email
use Illuminate\Support\Facades\Mail;

class AuthController extends Controller
{
    /**
     * =====================================
     * REGISTER USER BARU
     * =====================================
     * Fungsi ini digunakan untuk:
     * - Mendaftarkan user baru
     * - Membuat username otomatis
     * - Menyimpan data user
     * - Menghasilkan token autentikasi
     */
    public function register(Request $request)
    {
        // Validasi data input dari client
        // name wajib diisi
        // email harus unik
        // password minimal 6 karakter
        $request->validate([
            'name' => 'required|string|max:255',
            'email' => 'required|string|email|unique:users',
            'password' => 'required|string|min:6',
        ]);

        // Membuat username dasar:
        // - spasi dihapus
        // - diubah menjadi huruf kecil
        $baseUsername = strtolower(preg_replace('/\s+/', '', $request->name));
        $username = $baseUsername;

        // Jika username sudah ada,
        // tambahkan angka di belakang (misal: fani1, fani2)
        $counter = 1;
        while (User::where('username', $username)->exists()) {
            $username = $baseUsername . $counter;
            $counter++;
        }

        // Menyimpan data user ke database
        // Password disimpan dalam bentuk hash (aman)
        $user = User::create([
            'name'     => $request->name,
            'username' => $username,
            'email'    => $request->email,
            'password' => Hash::make($request->password),
        ]);

        // Membuat token API untuk autentikasi user
        // Token ini digunakan saat mengakses endpoint protected
        $token = $user->createToken('api_token')->plainTextToken;

        // Mengembalikan response JSON ke client
        return response()->json([
            'message' => 'Register success',
            'user'    => $user,
            'token'   => $token,
        ]);
    }

    /**
     * =====================================
     * LOGIN USER
     * =====================================
     * Fungsi ini digunakan untuk:
     * - Autentikasi user
     * - Login menggunakan email / username / nama
     * - Menghasilkan token baru
     */
    public function login(Request $request)
    {
        // Validasi input login
        $request->validate([
            'email' => 'required',
            'password' => 'required',
        ]);

        // Mencari user berdasarkan:
        // email ATAU name ATAU username
        $user = User::where('email', $request->email)
            ->orWhere('name', $request->email)
            ->orWhere('username', $request->email)
            ->first();

        // Jika user tidak ditemukan
        // atau password tidak sesuai
        if (!$user || !Hash::check($request->password, $user->password)) {
            throw ValidationException::withMessages([
                'email' => ['The provided credentials are incorrect.'],
            ]);
        }

        // Membuat token autentikasi baru
        $token = $user->createToken('api_token')->plainTextToken;

        // Response sukses login
        return response()->json([
            'message' => 'Login success',
            'user'    => $user,
            'token'   => $token,
        ]);
    }

    /**
     * =====================================
     * LOGOUT USER
     * =====================================
     * Menghapus token yang sedang aktif
     * sehingga user tidak bisa mengakses API lagi
     */
    public function logout(Request $request)
    {
        // Menghapus token yang digunakan saat ini
        $request->user()->currentAccessToken()->delete();

        return response()->json([
            'message' => 'Logged out successfully'
        ]);
    }

    /**
     * =====================================
     * ME (USER YANG SEDANG LOGIN)
     * =====================================
     * Mengembalikan data user
     * berdasarkan token yang dikirim
     */
    public function me(Request $request)
    {
        return response()->json([
            'user' => $request->user()
        ]);
    }

    /**
     * =====================================
     * FORGOT PASSWORD
     * =====================================
     * Mengirim kode verifikasi ke email user
     */
    public function forgotPassword(Request $request)
    {
        // Validasi email harus terdaftar
        $request->validate([
            'email' => 'required|email|exists:users,email',
        ]);

        // Membuat kode OTP 4 digit
        $code = random_int(1000, 9999);
        $email = $request->email;

        // Simpan atau update token reset password
        DB::table('password_reset_tokens')->updateOrInsert(
            ['email' => $email],
            [
                'token' => $code,
                'created_at' => now()
            ]
        );

        // Mengirim email berisi kode reset password
        Mail::raw("Kode reset password Anda adalah: $code", function ($message) use ($email) {
            $message->to($email)
                ->subject('Kode Reset Password');
        });

        return response()->json([
            'meta' => [
                'status_code' => 200,
                'status' => 'success',
                'message' => 'Kode verifikasi telah dikirim ke email.'
            ]
        ]);
    }

    /**
     * =====================================
     * VERIFIKASI KODE RESET PASSWORD
     * =====================================
     * Mengecek apakah kode OTP valid
     */
    public function verifyCode(Request $request)
    {
        // Validasi input
        $request->validate([
            'email' => 'required|email|exists:users,email',
            'code' => 'required|digits:4',
        ]);

        // Cek kecocokan email dan kode
        $record = DB::table('password_reset_tokens')
            ->where('email', $request->email)
            ->where('token', $request->code)
            ->first();

        // Jika kode salah atau tidak ditemukan
        if (!$record) {
            return response()->json([
                'meta' => [
                    'status_code' => 400,
                    'status' => 'error',
                    'message' => 'Kode tidak valid.'
                ]
            ], 400);
        }

        // Jika kode valid
        return response()->json([
            'meta' => [
                'status_code' => 200,
                'status' => 'success',
                'message' => 'Kode valid, lanjutkan ke reset password.'
            ]
        ]);
    }

    /**
     * =====================================
     * RESET PASSWORD
     * =====================================
     * Mengganti password lama dengan yang baru
     */
    public function resetPassword(Request $request)
    {
        // Validasi input reset password
        $request->validate([
            'email' => 'required|email|exists:users,email',
            'code' => 'required|digits:4',
            'password' => 'required|string|min:8',
        ]);

        // Cek token reset password
        $record = DB::table('password_reset_tokens')
            ->where('email', $request->email)
            ->where('token', $request->code)
            ->first();

        // Jika token tidak valid
        if (!$record) {
            return response()->json([
                'meta' => [
                    'status_code' => 400,
                    'status' => 'error',
                    'message' => 'Kode tidak valid.'
                ]
            ], 400);
        }

        // Update password user
        $user = User::where('email', $request->email)->first();
        $user->password = Hash::make($request->password);
        $user->save();

        // Hapus token reset password agar tidak bisa digunakan lagi
        DB::table('password_reset_tokens')
            ->where('email', $request->email)
            ->delete();

        return response()->json([
            'meta' => [
                'status_code' => 200,
                'status' => 'success',
                'message' => 'Password berhasil direset.'
            ]
        ]);
    }
}
