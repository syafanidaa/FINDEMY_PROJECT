<?php

namespace Tests;  // Namespace untuk semua file testing di folder 'tests'

use Illuminate\Foundation\Testing\TestCase as BaseTestCase; 
// Meng-extend TestCase dasar Laravel yang menyediakan fitur testing seperti HTTP requests, database testing, dll.

abstract class TestCase extends BaseTestCase
{
    // Kelas ini menjadi kelas dasar untuk semua unit test atau feature test
    // Bisa ditambahkan setup, helper, atau trait yang dibutuhkan semua test
}
