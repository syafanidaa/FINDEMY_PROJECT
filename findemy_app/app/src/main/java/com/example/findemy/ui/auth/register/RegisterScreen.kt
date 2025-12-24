package com.example.findemy.ui.auth.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.findemy.data.local.UserPreferences
import com.example.findemy.data.repository.AuthRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var agreePrivacy by remember { mutableStateOf(false) }
    var agreeTerms by remember { mutableStateOf(false) }

    var loading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val repo = remember { AuthRepository() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val prefs = remember { UserPreferences(context) }

    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            errorMessage = null
        }
    }

    Scaffold(snackbarHost = {
        SnackbarHost(hostState = snackbarHostState) { data ->
            Snackbar(
                snackbarData = data, containerColor = Color.Red,
                contentColor = Color.White,
                shape = RoundedCornerShape(8.dp), modifier = Modifier.padding(16.dp)
            )
        }
    }, topBar = {
        TopAppBar(
            title = {
                Text(
                    text = "Daftar dengan Email",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack, contentDescription = "Back"
                    )
                }
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            )
        )
    }) { paddingValues ->
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
                    .navigationBarsPadding(), horizontalAlignment = Alignment.Start
            ) {
                Spacer(Modifier.height(24.dp))

                // Nama Field
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Nama",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("Username", color = Color(0xFFBBBBBB)) },
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
                        placeholder = { Text("example@gmail.com", color = Color(0xFFBBBBBB)) },
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

                Spacer(Modifier.height(16.dp))

                // Konfirmasi Password Field
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Konfirmasi Password",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        placeholder = { Text("Password", color = Color(0xFFBBBBBB)) },
                        trailingIcon = {
                            IconButton(onClick = {
                                confirmPasswordVisible = !confirmPasswordVisible
                            }) {
                                Icon(
                                    imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                                    tint = Color(0xFF666666)
                                )
                            }
                        },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = agreePrivacy,
                        onCheckedChange = { agreePrivacy = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF4FC3F7)
                        )
                    )

                    Text(
                        text = "Saya setuju dengan ", fontSize = 12.sp, color = Color(0xFF666666)
                    )

                    TextButton(
                        onClick = {
                            keyboardController?.hide()
                            navController.navigate("privacy")
                        },
                        contentPadding = PaddingValues(0.dp),
                    ) {
                        Text(
                            text = "Kebijakan Privasi", fontSize = 12.sp, color = Color(0xFF4FC3F7)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = agreeTerms,
                        onCheckedChange = { agreeTerms = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF4FC3F7)
                        )
                    )

                    Text(
                        text = "Saya setuju dengan ", fontSize = 12.sp, color = Color(0xFF666666)
                    )

                    TextButton(
                        onClick = {
                            keyboardController?.hide()
                            navController.navigate("term")
                        }, contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Ketentuan Layanan", fontSize = 12.sp, color = Color(0xFF4FC3F7)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                // Daftar Button
                Button(
                    onClick = {
                        scope.launch {
                            keyboardController?.hide()
                            when {
                                name.isBlank() -> snackbarHostState.showSnackbar("Nama tidak boleh kosong")
                                email.isBlank() -> snackbarHostState.showSnackbar("Email tidak boleh kosong")
                                password.isBlank() -> snackbarHostState.showSnackbar("Password tidak boleh kosong")
                                confirmPassword.isBlank() -> snackbarHostState.showSnackbar("Konfirmasi password tidak boleh kosong")
                                password != confirmPassword -> snackbarHostState.showSnackbar("Password tidak cocok")
                                !agreePrivacy -> snackbarHostState.showSnackbar("Anda harus menyetujui Kebijakan Privasi")
                                !agreeTerms -> snackbarHostState.showSnackbar("Anda harus menyetujui Ketentuan Layanan")
                                else -> {

                                    loading = true
                                    val result = repo.register(name, email, password)
                                    loading = false

                                    result.onSuccess { data ->
                                        val token = data.token
                                        val name = data.user?.name
                                        val mail = data.user?.email

                                        if (token != null && name != null && mail != null) {
                                            prefs.saveUserData(token, name, mail)
                                            navController.navigate("base") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        } else {
                                            errorMessage = data.message ?: "Register gagal"
                                        }
                                    }.onFailure { e ->
                                        errorMessage = e.localizedMessage ?: "Terjadi kesalahan"
                                    }
                                }
                            }
                        }
                    },
                    enabled = !loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4FC3F7), contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Daftar", fontSize = 16.sp, fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}