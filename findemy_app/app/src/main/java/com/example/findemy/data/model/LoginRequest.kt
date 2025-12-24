```kotlin
package com.example.findemy.data.model
// Package untuk model request login

/**
 * Request untuk proses login user
 */
data class LoginRequest(
    val email: String,      // Email user
    val password: String   // Password user
)
```
