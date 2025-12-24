```kotlin
package com.example.findemy.data.model
// Package untuk model response lupa password

/**
 * Response API untuk fitur lupa password
 */
data class ForgotPasswordResponse(
    val meta: MetaResponse   // Informasi status response
)

/**
 * Informasi meta dari response API
 */
data class MetaResponse(
    val status_code: Int,    // Kode status
    val status: String,      // Status response
    val message: String      // Pesan dari server
)
```
