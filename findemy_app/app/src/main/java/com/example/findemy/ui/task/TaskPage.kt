package com.example.findemy.ui.task

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.findemy.data.model.Tugas
import com.example.findemy.data.repository.TugasRepository
import com.example.findemy.ui.components.AppDrawer
import com.example.findemy.ui.notification.NotificationScheduler
import com.example.findemy.ui.task.components.TimelineSection
import com.example.findemy.ui.task.components.EditTugasDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskPage(navController: NavController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val tugasRepository = remember { TugasRepository(context) }
    val snackbarHostState = remember { SnackbarHostState() }

    var tugasBelumSelesai by remember { mutableStateOf<List<Tugas>>(emptyList()) }
    var tugasSelesai by remember { mutableStateOf<List<Tugas>>(emptyList()) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var showEditDialog by remember { mutableStateOf(false) }
    var selectedTugas by remember { mutableStateOf<Tugas?>(null) }
    var showSuccessSnackbar by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    fun loadTugas() {
        scope.launch {
            isLoading = true
            errorMessage = null

            try {
                val belumSelesaiResult = tugasRepository.getTugass("belum selesai")
                belumSelesaiResult.onSuccess { response ->
                    tugasBelumSelesai = response.data
                }.onFailure { e ->
                    errorMessage = e.message
                }

                val selesaiResult = tugasRepository.getTugass("selesai")
                selesaiResult.onSuccess { response ->
                    tugasSelesai = response.data
                }.onFailure { e ->
                    errorMessage = e.message
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Terjadi kesalahan"
            } finally {
                isLoading = false
            }

            if (errorMessage?.contains("Sesi Anda telah berakhir") == true ||
                errorMessage?.contains("Token tidak ditemukan") == true
            ) {
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        loadTugas()
    }

    // Show success snackbar
    LaunchedEffect(showSuccessSnackbar) {
        if (showSuccessSnackbar) {
            snackbarHostState.showSnackbar(
                message = successMessage,
                duration = SnackbarDuration.Short
            )
            showSuccessSnackbar = false
        }
    }

    // Dialog edit tugas
    if (showEditDialog && selectedTugas != null) {
        EditTugasDialog(
            tugas = selectedTugas!!,
            onDismiss = {
                showEditDialog = false
                selectedTugas = null
            },
            onConfirm = { request ->
                scope.launch {
                    isLoading = true
                    val result = tugasRepository.updateTugas(selectedTugas!!.id, request)
                    result.onSuccess { response ->
                        showEditDialog = false
                        selectedTugas = null
                        successMessage = "Tugas berhasil diperbarui"
                        showSuccessSnackbar = true

                        NotificationScheduler.cancelNotification(context, "tugas", response.id)

                        if (response.pasang_pengingat) {
                            NotificationScheduler.scheduleTugasNotification(context, response)
                        }
                        loadTugas()
                    }.onFailure { exception ->
                        errorMessage = exception.message
                        isLoading = false
                    }
                }
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                navController = navController,
                onItemSelected = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = Color.Black,
                        navigationIconContentColor = Color.Black
                    ),
                    title = { Text(text = "") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    windowInsets = WindowInsets(0, 0, 0, 0)
                )
            },
            containerColor = Color.White,
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { padding ->
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF8A93D7))
                    }
                }

                errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = errorMessage ?: "Terjadi kesalahan",
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { loadTugas() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF8A93D7)
                                )
                            ) {
                                Text("Coba Lagi")
                            }
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        if (tugasBelumSelesai.isNotEmpty()) {
                            item {
                                TimelineSection(
                                    title = "Tugas Belum Selesai",
                                    tugasList = tugasBelumSelesai,
                                    onEditTugas = { tugas ->
                                        selectedTugas = tugas
                                        showEditDialog = true
                                    },
                                    onDeleteTugas = { tugas ->
                                        scope.launch {
                                            isLoading = true
                                            val result = tugasRepository.deleteTugas(tugas.id)
                                            result.onSuccess {
                                                successMessage = "Tugas berhasil dihapus"
                                                showSuccessSnackbar = true
                                                NotificationScheduler.cancelNotification(context, "tugas", tugas.id)
                                                loadTugas()
                                            }.onFailure { exception ->
                                                errorMessage = exception.message
                                                isLoading = false
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        if (tugasSelesai.isNotEmpty()) {
                            item {
                                TimelineSection(
                                    title = "Tugas Selesai",
                                    tugasList = tugasSelesai,
                                    onEditTugas = { tugas ->
                                        selectedTugas = tugas
                                        showEditDialog = true
                                    },
                                    onDeleteTugas = { tugas ->
                                        scope.launch {
                                            isLoading = true
                                            val result = tugasRepository.deleteTugas(tugas.id)
                                            result.onSuccess {
                                                successMessage = "Tugas berhasil dihapus"
                                                showSuccessSnackbar = true
                                                NotificationScheduler.cancelNotification(context, "tugas", tugas.id)
                                                loadTugas()
                                            }.onFailure { exception ->
                                                errorMessage = exception.message
                                                isLoading = false
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        if (tugasBelumSelesai.isEmpty() && tugasSelesai.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Tidak ada tugas untuk hari ini.")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}