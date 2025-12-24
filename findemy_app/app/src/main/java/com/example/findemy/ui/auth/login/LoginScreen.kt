package com.example.findemy.ui.auth.login

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.findemy.R
import com.example.findemy.data.local.UserPreferences
import com.example.findemy.data.repository.AuthRepository
import com.example.findemy.data.repository.EventRepository
import com.example.findemy.data.repository.JadwalRepository
import com.example.findemy.data.repository.TugasRepository
import com.example.findemy.ui.notification.NotificationScheduler
import kotlinx.coroutines.launch
import com.google.accompanist.systemuicontroller.rememberSystemUiController



@Composable
fun LoginScreen(navController: NavController) {
    val systemUiController = rememberSystemUiController()
    val context = LocalContext.current;

    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.White,
            darkIcons = true
        )
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val repo = remember { AuthRepository() }
    val repoJadwal = remember { JadwalRepository(context) }
    val repoEvent = remember { EventRepository(context) }
    val repoTugas = remember { TugasRepository(context) }
    val scope = rememberCoroutineScope()
    val prefs = remember { UserPreferences(context) }

    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            errorMessage = null
        }
    }

    Scaffold(

        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .navigationBarsPadding(),
                snackbar = { snackbarData ->
                    Snackbar(
                        containerColor = Color.Red,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = snackbarData.visuals.message,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .statusBarsPadding()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1f))

                // Email Field
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Email",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("Email", color = Color(0xFFBBBBBB)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFF666666)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFEEEEEE),
                            focusedBorderColor = Color(0xFF4FC3F7),
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedContainerColor = Color(0xFFF5F5F5)
                        ),
                        singleLine = true
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Password Field
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Password",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Password", color = Color(0xFFBBBBBB)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color(0xFF666666)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = Color(0xFF666666)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFEEEEEE),
                            focusedBorderColor = Color(0xFF4FC3F7),
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedContainerColor = Color(0xFFF5F5F5)
                        ),
                        singleLine = true
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Login Button
                Button(
                    onClick = {
                        scope.launch {
                            keyboardController?.hide()
                            if (email.isBlank() || password.isBlank()) {
                                errorMessage = "Email dan password wajib diisi"
                                return@launch
                            }

                            loading = true
                            val result = repo.login(email, password)
                            loading = false

                            result.onSuccess { data ->
                                val token = data.token
                                val name = data.user?.name
                                val mail = data.user?.email

                                if (token != null && name != null && mail != null) {
                                    prefs.saveUserData(token, name, mail)
                                    scope.launch {
                                        try {

                                            val jadwalResult = repoJadwal.getJadwals()
                                            val tugasResult = repoTugas.getTugass()
                                            val eventResult = repoEvent.getEvents()

                                            // Extract data dari Result<JadwalListResponse>
                                            val jadwalList = jadwalResult.getOrNull()?.data ?: emptyList()
                                            val tugasList = tugasResult.getOrNull()?.data ?: emptyList()
                                            val eventList = eventResult.getOrNull()?.data ?: emptyList()

                                            // Jadwalkan notifikasi
                                            NotificationScheduler.scheduleAllNotifications(
                                                context = context,
                                                jadwalList = jadwalList,
                                                tugasList = tugasList,
                                                eventList = eventList
                                            )
                                        } catch (e: Exception) {
                                        }
                                    }
                                    navController.navigate("base") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                } else {
                                    errorMessage = data.message ?: "Login gagal"
                                }
                            }.onFailure { e ->
                                errorMessage = e.localizedMessage ?: "Terjadi kesalahan"
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
                        text = if (loading) "Memproses..." else "Masuk",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Register Button
                Button(
                    onClick = { navController.navigate("register") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFA726),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Daftar dengan Email",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(16.dp))

                TextButton(onClick = {
                    keyboardController?.hide()
                    navController.navigate("forgot_password")
                }) {
                    Text(
                        text = "Lupa Password ?",
                        fontSize = 14.sp,
                        color = Color(0xFF666666)
                    )
                }

                Spacer(Modifier.height(24.dp))

                Image(
                    painter = painterResource(id = R.drawable.auth_maskot),
                    contentDescription = "Gambar Maskot",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}
