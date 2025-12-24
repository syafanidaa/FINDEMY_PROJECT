<?php

namespace App\Http\Controllers;

use App\Models\Event;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class EventController extends Controller
{
    /**
     * Menampilkan semua event milik user yang sedang login
     */
    public function index()
    {
        // Ambil event berdasarkan user login
        $events = Event::where('user_id', Auth::id())->get();

        return response()->json([
            'message' => 'Berhasil mendapatkan daftar event',
            'data' => $events
        ]);
    }

    /**
     * Menyimpan event baru
     */
    public function store(Request $request)
    {
        // Validasi input event
        $request->validate([
            'judul' => 'required|string|max:255',
            'tanggal_mulai' => 'required|date',
            'tanggal_selesai' => 'required|date|after_or_equal:tanggal_mulai',
        ]);

        // Simpan event ke database
        $event = Event::create([
            'user_id' => $request->user()->id,
            'judul' => $request->judul,
            'tanggal_mulai' => $request->tanggal_mulai,
            'tanggal_selesai' => $request->tanggal_selesai,
            'pasang_pengingat' => $request->pasang_pengingat,
        ]);

        return response()->json([
            'message' => 'Event created successfully',
            'data' => $event
        ], 201);
    }

    /**
     * Menampilkan detail event (belum digunakan)
     */
    public function show(string $id)
    {
        //
    }

    /**
     * Mengupdate data event
     */
    public function update(Request $request, $id)
    {
        // Cari event berdasarkan ID
        $event = Event::find($id);

        // Jika event tidak ditemukan
        if (!$event) {
            return response()->json([
                'message' => 'Event not found'
            ], 404);
        }

        // Validasi data update
        $request->validate([
            'judul' => 'required|string|max:255',
            'tanggal_mulai' => 'required|date',
            'tanggal_selesai' => 'required|date|after_or_equal:tanggal_mulai',
        ]);

        // Update data event
        $event->update($request->only([
            'judul', 'tanggal_mulai', 'tanggal_selesai'
        ]));

        return response()->json([
            'message' => 'Event updated successfully',
            'data' => $event
        ], 200);
    }

    /**
     * Menghapus event
     */
    public function destroy($id)
    {
        // Cari event berdasarkan ID
        $event = Event::find($id);

        // Jika event tidak ditemukan
        if (!$event) {
            return response()->json([
                'message' => 'Event not found'
            ], 404);
        }

        // Hapus event
        $event->delete();

        return response()->json([
            'message' => 'Event deleted successfully'
        ], 200);
    }
}
