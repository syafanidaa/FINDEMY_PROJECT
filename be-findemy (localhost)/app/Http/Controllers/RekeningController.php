<?php

namespace App\Http\Controllers;

use App\Models\Rekening;
use App\Models\Transaksi;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class RekeningController extends Controller
{
    /**
     * Menampilkan daftar rekening milik user
     */
    public function index(Request $request)
    {
        // Ambil semua rekening berdasarkan user login
        $rekenings = Rekening::where('user_id', Auth::id())->get();

        return response()->json([
            'message' => 'Rekenings retrieved successfully',
            'data' => $rekenings
        ]);
    }

    /**
     * Menambahkan rekening baru
     */
    public function store(Request $request)
    {
        // Validasi input rekening
        $request->validate([
            'nama' => 'required|string|max:255',
            'saldo' => 'required',
        ]);

        // Simpan rekening baru
        $rekening = Rekening::create([
            'user_id' => $request->user()->id,
            'nama' => $request->nama,
            'saldo' => $request->saldo,
        ]);

        // Catat saldo awal sebagai transaksi pemasukan
        Transaksi::create([
            'user_id' => $request->user()->id,
            'rekening_id' => $rekening->id,
            'jenis' => 'pemasukan',
            'keterangan' => 'Saldo Awal Rekening',
            'jumlah' => $request->saldo,
        ]);

        return response()->json([
            'message' => 'Rekening berhasil ditambahkan.',
            'data' => $rekening
        ], 201);
    }

    /**
     * Menampilkan detail rekening (belum digunakan)
     */
    public function show(string $id)
    {
        //
    }

    /**
     * Memperbarui data rekening
     */
    public function update(Request $request, $id)
    {
        // Validasi input update
        $request->validate([
            'nama' => 'required|string|max:255',
            'saldo' => 'required|numeric',
        ]);

        // Ambil rekening milik user
        $rekening = Rekening::where('user_id', $request->user()->id)->find($id);

        // Jika rekening tidak ditemukan
        if (!$rekening) {
            return response()->json([
                'message' => 'Rekening tidak ditemukan atau tidak memiliki akses.'
            ], 404);
        }

        // Hitung selisih saldo lama dan baru
        $saldoLama = $rekening->saldo;
        $saldoBaru = $request->saldo;
        $selisih = $saldoBaru - $saldoLama;

        // Update data rekening
        $rekening->update([
            'nama' => $request->nama,
            'saldo' => $saldoBaru,
        ]);

        // Catat penyesuaian saldo sebagai transaksi
        if ($selisih != 0) {
            Transaksi::create([
                'user_id' => $request->user()->id,
                'rekening_id' => $rekening->id,
                'jenis' => $selisih > 0 ? 'pemasukan' : 'pengeluaran',
                'keterangan' => 'Penyesuaian saldo rekening',
                'jumlah' => abs($selisih),
            ]);
        }

        return response()->json([
            'message' => 'Rekening berhasil diperbarui.',
            'data' => $rekening
        ]);
    }

    /**
     * Menghapus rekening
     */
    public function destroy(Request $request, $id)
    {
        // Ambil rekening milik user
        $rekening = Rekening::where('user_id', $request->user()->id)->find($id);

        // Jika tidak ditemukan atau tidak punya akses
        if (!$rekening) {
            return response()->json([
                'message' => 'Rekening tidak ditemukan atau tidak memiliki akses.'
            ], 404);
        }

        // Jika saldo masih ada, catat sebagai pengeluaran
        if ($rekening->saldo > 0) {
            Transaksi::create([
                'user_id' => $request->user()->id,
                'rekening_id' => $rekening->id,
                'jenis' => 'pengeluaran',
                'jumlah' => $rekening->saldo,
                'keterangan' => 'Rekening dihapus',
                'tanggal' => now(),
            ]);
        }

        // Hapus rekening
        $rekening->delete();

        return response()->json([
            'message' => 'Rekening berhasil dihapus.'
        ], 200);
    }
}
