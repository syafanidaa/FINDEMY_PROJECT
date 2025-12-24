package com.example.findemy

// Import anotasi @Test dari JUnit untuk menandai fungsi sebagai unit test
import org.junit.Test

// Import fungsi assertEquals untuk membandingkan nilai yang diharapkan dan hasil aktual
import org.junit.Assert.*

/**
 * Unit test sederhana yang dijalankan di local JVM (bukan di Android device).
 * Digunakan untuk mengecek logika dasar program.
 */
class ExampleUnitTest {

    // Anotasi @Test menandakan fungsi ini adalah test case
    @Test
    fun addition_isCorrect() {
        // Mengecek apakah hasil 2 + 2 sama dengan 4
        assertEquals(4, 2 + 2)
    }
}
