<?php

namespace App\Http\Controllers;

use App\Models\Tugas;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class TugasController extends Controller
{
    /**
     * Method untuk menampilkan seluruh data tugas milik user yang sedang login.
     * Data dapat difilter berdasarkan hari dan status jika parameter dikirim.
     */
    public function index(Request $request)
    {
        // Query awal untuk mengambil tugas beserta relasi jadwal berdasarkan user login
        $query = Tugas::with('jadwal')->where('user_id',  Auth::id());

        // Filter tugas berdasarkan hari (jika ada)
        if ($request->has('hari') && !empty($request->hari)) {
            $query->where('hari', $request->hari);
        }

        // Filter tugas berdasarkan status (jika ada)
        if ($request->has('status') && !empty($request->status)) {
            $query->where('status', $request->status);
        }

        // Mengambil seluruh data tugas dan mengurutkannya berdasarkan deadline
        $tugass = $query->orderBy('deadline', 'asc')->get();

        // Mengembalikan response JSON berisi daftar tugas
        return response()->json([
            'message' => 'Berhasil mendapatkan daftar tugas',
            'data' => $tugass
        ]);
    }

    /**
     * Method untuk menyimpan data tugas baru ke dalam database.
     */
    public function store(Request $request)
    {
        // Validasi input dari user
        $request->validate([
            'jadwal_id' => 'required|exists:jadwal,id',
            'judul' => 'required|string|max:255',
            'deskripsi' => 'required|string|max:255',
            'deadline' => 'required|string',
            'status' => 'required|string',
            'pasang_pengingat' => 'required',
        ]);

        // Menyimpan data tugas ke database
        $tugas = Tugas::create([
            'user_id' => $request->user()->id,
            'jadwal_id' => $request->jadwal_id,
            'judul' => $request->judul,
            'deskripsi' => $request->deskripsi,
            'deadline' => $request->deadline,
            'status' => $request->status,
            'pasang_pengingat' => $request->pasang_pengingat,
        ]);

        // Response jika tugas berhasil ditambahkan
        return response()->json([
            'message' => 'Tugas berhasil ditambahkan.',
            'data' => $tugas
        ], 201);
    }

    /**
     * Method untuk menampilkan detail satu tugas.
     * (Belum digunakan)
     */
    public function show(string $id)
    {
        //
    }

    /**
     * Method untuk memperbarui data tugas berdasarkan ID.
     */
    public function update(Request $request, $id)
    {
        // Validasi data input
        $request->validate([
            'jadwal_id' => 'required|exists:jadwal,id',
            'judul' => 'required|string|max:255',
            'deskripsi' => 'required|string|max:255',
            'deadline' => 'required|string',
            'status' => 'required|string',
            'pasang_pengingat' => 'required',
        ]);

        // Mengambil data tugas milik user yang sedang login
        $tugas = Tugas::where('user_id', $request->user()->id)->find($id);

        // Jika tugas tidak ditemukan
        if (!$tugas) {
            return response()->json([
                'message' => 'Tugas tidak ditemukan atau tidak memiliki akses.'
            ], 404);
        }

        // Memperbarui data tugas
        $tugas->update([
            'user_id' => $request->user()->id,
            'jadwal_id' => $request->jadwal_id,
            'judul' => $request->judul,
            'deskripsi' => $request->deskripsi,
            'deadline' => $request->deadline,
            'status' => $request->status,
            'pasang_pengingat' => $request->pasang_pengingat,
        ]);

        // Response jika update berhasil
        return response()->json([
            'message' => 'Tugas berhasil diperbarui.',
            'data' => $tugas
        ]);
    }

    /**
     * Method untuk menghapus data tugas berdasarkan ID.
     */
    public function destroy(Request $request, $id)
    {
        // Mengambil tugas berdasarkan user login
        $tugas = Tugas::where('user_id', $request->user()->id)->find($id);

        // Jika tugas tidak ditemukan
        if (!$tugas) {
            return response()->json([
                'message' => 'Tugas tidak ditemukan atau tidak memiliki akses.'
            ], 404);
        }

        // Menghapus data tugas
        $tugas->delete();

        // Response jika penghapusan berhasil
        return response()->json([
            'message' => 'Tugas berhasil dihapus.'
        ], 200);
    }
}
