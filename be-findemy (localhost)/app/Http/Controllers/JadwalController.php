<?php

namespace App\Http\Controllers;

use App\Models\Jadwal;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class JadwalController extends Controller
{
    /**
     * Menampilkan daftar jadwal milik user
     * Bisa difilter berdasarkan hari
     */
    public function index(Request $request)
    {
        // Ambil jadwal berdasarkan user login
        $query = Jadwal::where('user_id', Auth::id());

        // Filter jadwal berdasarkan hari (opsional)
        if ($request->has('hari') && !empty($request->hari)) {
            $query->where('hari', $request->hari);
        }

        // Urutkan berdasarkan jam mulai
        $jadwals = $query->orderBy('jam_mulai', 'asc')->get();

        return response()->json([
            'message' => 'Berhasil mendapatkan daftar Jadwal',
            'data' => $jadwals
        ]);
    }

    /**
     * Menambahkan jadwal baru
     */
    public function store(Request $request)
    {
        // Validasi input jadwal
        $request->validate([
            'mata_kuliah' => 'required|string|max:255',
            'dosen' => 'required|string|max:255',
            'ruangan' => 'required|string|max:255',
            'hari' => 'required|string|max:20',
            'jam_mulai' => 'required|string',
            'jam_selesai' => 'required|string',
            'pasang_pengingat' => 'required',
        ]);

        // Simpan jadwal ke database
        $jadwal = Jadwal::create([
            'user_id' => $request->user()->id,
            'mata_kuliah' => $request->mata_kuliah,
            'dosen' => $request->dosen,
            'ruangan' => $request->ruangan,
            'hari' => $request->hari,
            'jam_mulai' => $request->jam_mulai,
            'jam_selesai' => $request->jam_selesai,
            'pasang_pengingat' => $request->pasang_pengingat,
        ]);

        return response()->json([
            'message' => 'Jadwal berhasil ditambahkan.',
            'data' => $jadwal
        ], 201);
    }

    /**
     * Menampilkan detail jadwal (belum digunakan)
     */
    public function show(string $id)
    {
        //
    }

    /**
     * Memperbarui data jadwal
     */
    public function update(Request $request, $id)
    {
        // Validasi input update
        $request->validate([
            'mata_kuliah' => 'required|string|max:255',
            'dosen' => 'required|string|max:255',
            'ruangan' => 'required|string|max:255',
            'hari' => 'required|string|max:20',
            'jam_mulai' => 'required|string',
            'jam_selesai' => 'required|string',
            'pasang_pengingat' => 'required',
        ]);

        // Ambil jadwal milik user
        $jadwal = Jadwal::where('user_id', $request->user()->id)->find($id);

        // Jika jadwal tidak ditemukan atau bukan milik user
        if (!$jadwal) {
            return response()->json([
                'message' => 'Jadwal tidak ditemukan atau tidak memiliki akses.'
            ], 404);
        }

        // Update data jadwal
        $jadwal->update([
            'mata_kuliah' => $request->mata_kuliah,
            'dosen' => $request->dosen,
            'ruangan' => $request->ruangan,
            'hari' => $request->hari,
            'jam_mulai' => $request->jam_mulai,
            'jam_selesai' => $request->jam_selesai,
            'pasang_pengingat' => $request->pasang_pengingat,
        ]);

        return response()->json([
            'message' => 'Jadwal berhasil diperbarui.',
            'data' => $jadwal
        ]);
    }

    /**
     * Menghapus jadwal
     */
    public function destroy(Request $request, $id)
    {
        // Ambil jadwal milik user
        $jadwal = Jadwal::where('user_id', $request->user()->id)->find($id);

        // Jika tidak ditemukan atau tidak punya akses
        if (!$jadwal) {
            return response()->json([
                'message' => 'Jadwal tidak ditemukan atau tidak memiliki akses.'
            ], 404);
        }

        // Hapus jadwal
        $jadwal->delete();

        return response()->json([
            'message' => 'Jadwal berhasil dihapus.'
        ], 200);
    }
}
