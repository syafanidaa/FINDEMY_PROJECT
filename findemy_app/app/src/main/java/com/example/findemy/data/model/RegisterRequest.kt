```kotlin
package com.example.findemy.data.model
// Package untuk model request registrasi

/**
 * Request untuk proses registrasi user
 */
data class RegisterRequest(
    val name: String,       // Nama user
    val email: String,      // Email user
    val password: String   // Password user
)
```
