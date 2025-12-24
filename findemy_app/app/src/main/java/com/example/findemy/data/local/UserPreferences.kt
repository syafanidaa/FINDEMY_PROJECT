package com.example.findemy.data.local
// Package ini berisi pengelolaan data lokal aplikasi (local storage)

import android.content.Context
// Context diperlukan untuk mengakses DataStore

import androidx.datastore.preferences.core.edit
// Digunakan untuk mengubah (menyimpan / menghapus) data di DataStore

import androidx.datastore.preferences.core.stringPreferencesKey
// Digunakan untuk membuat key bertipe String di DataStore

import androidx.datastore.preferences.core.booleanPreferencesKey
// Digunakan untuk membuat key bertipe Boolean di DataStore

import androidx.datastore.preferences.preferencesDataStore
// Extension untuk membuat instance Preferences DataStore

import kotlinx.coroutines.flow.Flow
// Flow digunakan untuk membaca data secara asynchronous & reactive

import kotlinx.coroutines.flow.first
// Digunakan untuk mengambil nilai pertama dari Flow (sekali ambil)

import kotlinx.coroutines.flow.map
// Digunakan untuk memetakan data dari DataStore ke tipe yang diinginkan

/**
 * Extension property DataStore
 * ----------------------------
 * Membuat DataStore dengan nama "user_prefs"
 * DataStore ini akan digunakan untuk menyimpan data user
 */
private val Context.dataStore by preferencesDataStore("user_prefs")

/**
 * Class UserPreferences
 * ---------------------
 * Class ini bertanggung jawab untuk:
 * - Menyimpan data user (token, nama, email)
 * - Menyimpan status onboarding
 * - Mengambil dan menghapus data user
 */
class UserPreferences(private val context: Context) {

    /**
     * Companion Object
     * ----------------
     * Berisi key-key yang digunakan dalam DataStore
     * Key ini bersifat static dan bisa diakses tanpa instance class
     */
    companion object {

        // Key untuk menyimpan token login user
        val KEY_TOKEN = stringPreferencesKey("token")

        // Key untuk menyimpan nama user
        val KEY_NAME = stringPreferencesKey("name")

        // Key untuk menyimpan email user
        val KEY_EMAIL = stringPreferencesKey("email")

        // Key untuk menyimpan status apakah onboarding sudah selesai
        val KEY_ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
    }

    /**
     * Flow token
     * ----------
     * Mengambil token dari DataStore dalam bentuk Flow
     * Nilai bisa null jika token belum disimpan
     */
    val token: Flow<String?> =
        context.dataStore.data.map { it[KEY_TOKEN] }

    /**
     * Flow name
     * ---------
     * Mengambil nama user dari DataStore
     */
    val name: Flow<String?> =
        context.dataStore.data.map { it[KEY_NAME] }

    /**
     * Flow email
     * ----------
     * Mengambil email user dari DataStore
     */
    val email: Flow<String?> =
        context.dataStore.data.map { it[KEY_EMAIL] }

    /**
     * Flow onboardingDone
     * -------------------
     * Mengambil status onboarding
     * Jika belum ada data, default = false
     */
    val onboardingDone: Flow<Boolean> =
        context.dataStore.data.map { it[KEY_ONBOARDING_DONE] ?: false }

    /**
     * Fungsi saveUserData
     * -------------------
     * Menyimpan data user setelah login atau register
     * Disimpan ke DataStore secara asynchronous
     */
    suspend fun saveUserData(token: String, name: String, email: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TOKEN] = token
            prefs[KEY_NAME] = name
            prefs[KEY_EMAIL] = email
        }
    }

    /**
     * Fungsi setOnboardingDone
     * ------------------------
     * Menandai bahwa onboarding sudah selesai
     * Biasanya dipanggil setelah user menyelesaikan onboarding
     */
    suspend fun setOnboardingDone() {
        context.dataStore.edit { prefs ->
            prefs[KEY_ONBOARDING_DONE] = true
        }
    }

    /**
     * Fungsi isOnboardingDone
     * ----------------------
     * Mengambil status onboarding secara langsung (Boolean)
     * Menggunakan first() untuk mengambil nilai Flow sekali saja
     */
    suspend fun isOnboardingDone(): Boolean {
        return onboardingDone.first()
    }

    /**
     * Fungsi getToken
     * ---------------
     * Mengambil token user secara langsung
     * Digunakan misalnya untuk cek login atau request API
     */
    suspend fun getToken(): String? {
        return token.first()
    }

    /**
     * Fungsi clear
     * ------------
     * Menghapus semua data yang tersimpan di DataStore
     * Biasanya dipanggil saat logout
     */
    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
