package com.example.findemy.ui.auth.forgot

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.findemy.data.repository.AuthRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyCodeScreen(navController: NavController, email: String) {
    var code by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    val repo = remember { AuthRepository() }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Verifikasi Kode",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(40.dp))

                Text(
                    text = "Masukkan kode 4 digit yang telah dikirim ke",
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = email,
                    fontSize = 14.sp,
                    color = Color(0xFF333333),
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(32.dp))

                // Code Input Field
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Kode Verifikasi",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = code,
                        onValueChange = { if (it.length <= 4) code = it },
                        placeholder = { Text("0000", color = Color(0xFFBBBBBB)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFEEEEEE),
                            focusedBorderColor = Color(0xFF4FC3F7),
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedContainerColor = Color(0xFFF5F5F5)
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Verify Button
                Button(
                    onClick = {
                        scope.launch {
                            keyboardController?.hide()
                            if (code.isBlank()) {
                                snackbarHostState.showSnackbar("Kode tidak boleh kosong")
                                return@launch
                            }

                            if (code.length != 4) {
                                snackbarHostState.showSnackbar("Kode harus 4 digit")
                                return@launch
                            }

                            loading = true
                            val result = repo.verifyCode(email, code)
                            loading = false

                            result.onSuccess { response ->
                                navController.navigate("reset_password/$email/$code")
                            }.onFailure { e ->
                                snackbarHostState.showSnackbar(
                                    e.localizedMessage ?: "Terjadi kesalahan"
                                )
                            }
                        }
                    },
                    enabled = !loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4FC3F7),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (loading) "Memverifikasi..." else "Verifikasi",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(16.dp))

                TextButton(
                    onClick = {
                        scope.launch {
                            loading = true
                            val result = repo.forgotPassword(email)
                            loading = false

                            result.onSuccess { response ->
                                snackbarHostState.showSnackbar("Kode berhasil dikirim ulang")
                            }.onFailure { e ->
                                snackbarHostState.showSnackbar(
                                    e.localizedMessage ?: "Gagal mengirim ulang kode"
                                )
                            }
                        }
                    }
                ) {
                    Text(
                        text = "Kirim Ulang Kode",
                        fontSize = 14.sp,
                        color = Color(0xFF4FC3F7)
                    )
                }
            }
        }
    }
}