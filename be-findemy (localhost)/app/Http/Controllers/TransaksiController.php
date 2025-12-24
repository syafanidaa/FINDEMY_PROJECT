<?php

namespace App\Http\Controllers;

use App\Models\Rekening;
use App\Models\Transaksi;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class TransaksiController extends Controller
{
    /**
     * Menampilkan daftar transaksi user
     * Bisa difilter berdasarkan bulan dan tahun
     */
    public function index(Request $request)
    {
        // Ambil ID user login
        $userId = Auth::id();

        // Ambil filter bulan & tahun (default: bulan dan tahun sekarang)
        $bulan = $request->input('bulan', date('m'));
        $tahun = $request->input('tahun', date('Y'));

        // Ambil transaksi beserta rekening
        $transactions = Transaksi::with(['rekening'])
            ->where('user_id', $userId)
            ->whereMonth('created_at', $bulan)
            ->whereYear('created_at', $tahun)
            ->orderBy('created_at', 'desc')
            ->get();

        // Hitung total pemasukan bulan tertentu
        $totalPemasukan = Transaksi::where('user_id', $userId)
            ->where('jenis', 'pemasukan')
            ->whereMonth('created_at', $bulan)
            ->whereYear('created_at', $tahun)
            ->sum('jumlah');

        // Hitung total pengeluaran bulan tertentu
        $totalPengeluaran = Transaksi::where('user_id', $userId)
            ->where('jenis', 'pengeluaran')
            ->whereMonth('created_at', $bulan)
            ->whereYear('created_at', $tahun)
            ->sum('jumlah');

        // Hitung total pemasukan keseluruhan
        $totalPemasukanAll = Transaksi::where('user_id', $userId)
            ->where('jenis', 'pemasukan')
            ->sum('jumlah');

        // Hitung total pengeluaran keseluruhan
        $totalPengeluaranAll = Transaksi::where('user_id', $userId)
            ->where('jenis', 'pengeluaran')
            ->sum('jumlah');

        // Hitung saldo akhir
        $saldo = $totalPemasukanAll - $totalPengeluaranAll;

        // Hitung selisih pemasukan dan pengeluaran bulan ini
        $selisih = $totalPemasukan - $totalPengeluaran;

        return response()->json([
            'message' => 'Success',
            'data' => [
                'bulan' => $bulan,
                'tahun' => $tahun,
                'transaksi' => $transactions,
                'saldo' => strval($saldo),
                'pemasukan' => strval($totalPemasukan),
                'pengeluaran' => strval($totalPengeluaran),
                'selisih' => strval($selisih),
            ]
        ]);
    }

    /**
     * Menambahkan transaksi baru
     * Sekaligus memperbarui saldo rekening
     */
    public function store(Request $request)
    {
        // Validasi input transaksi
        $request->validate([
            'rekening_id' => 'required|exists:rekening,id',
            'jenis' => 'required|string|in:pemasukan,pengeluaran',
            'keterangan' => 'required|string|max:255',
            'jumlah' => 'required|numeric|min:0',
        ]);

        $user = $request->user();

        // Ambil rekening milik user
        $rekening = Rekening::where('user_id', $user->id)
            ->findOrFail($request->rekening_id);

        // Simpan transaksi
        $transaksi = Transaksi::create([
            'user_id' => $user->id,
            'rekening_id' => $rekening->id,
            'jenis' => $request->jenis,
            'keterangan' => $request->keterangan,
            'jumlah' => $request->jumlah,
        ]);

        // Update saldo rekening berdasarkan jenis transaksi
        if ($request->jenis === 'pemasukan') {
            $rekening->saldo += $request->jumlah;
        } elseif ($request->jenis === 'pengeluaran') {
            $rekening->saldo -= $request->jumlah;
        }

        $rekening->save();

        return response()->json([
            'message' => 'Transaksi berhasil ditambahkan dan saldo rekening diperbarui.',
            'data' => [
                'transaksi' => $transaksi,
                'rekening' => $rekening
            ]
        ], 201);
    }

    /**
     * Menampilkan detail transaksi (belum digunakan)
     */
    public function show(string $id)
    {
        //
    }

    /**
     * Update transaksi (belum digunakan)
     */
    public function update(Request $request, string $id)
    {
        //
    }

    /**
     * Menghapus transaksi (belum digunakan)
     */
    public function destroy(string $id)
    {
        //
    }
}
