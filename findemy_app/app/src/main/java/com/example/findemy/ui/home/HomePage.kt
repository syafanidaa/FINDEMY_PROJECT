package com.example.findemy.ui.home

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
import com.example.findemy.data.model.Jadwal
import com.example.findemy.data.model.RekapTransaksi
import com.example.findemy.data.model.Tugas
import com.example.findemy.data.repository.JadwalRepository
import com.example.findemy.data.repository.TransaksiRepository
import com.example.findemy.data.repository.TugasRepository
import com.example.findemy.ui.components.AppDrawer
import com.example.findemy.ui.home.components.*
import com.example.findemy.ui.task.components.EditTugasDialog
import com.example.findemy.ui.task.components.TimelineSection
import com.example.findemy.ui.transaction.components.SaldoSection
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.work.*
import java.util.concurrent.TimeUnit



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(navController: NavController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current




    // State Jadwal
    var jadwals by remember { mutableStateOf<List<Jadwal>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // State Tugas
    var tugasList by remember { mutableStateOf<List<Tugas>>(emptyList()) }
    var isLoadingTugas by remember { mutableStateOf(false) }
    var errorMessageTugas by remember { mutableStateOf<String?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedTugas by remember { mutableStateOf<Tugas?>(null) }
    var showSuccessSnackbar by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    // State Transaksi
    var rekap by remember { mutableStateOf<RekapTransaksi?>(null) }
    var isLoadingTransaksi by remember { mutableStateOf(false) }
    var errorMessageTransaksi by remember { mutableStateOf<String?>(null) }

    val repository = remember { JadwalRepository(context) }
    val tugasRepository = remember { TugasRepository(context) }
    val transaksiRepository = remember { TransaksiRepository(context) }

    // Ambil bulan dan tahun saat ini
    val calendar = remember { Calendar.getInstance() }
    val currentMonth = remember { calendar.get(Calendar.MONTH) + 1 }
    val currentYear = remember { calendar.get(Calendar.YEAR) }

    // Ambil nama hari ini
    val hariIni = remember {
        val locale = Locale("id", "ID")
        SimpleDateFormat("EEEE", locale).format(Date()).replaceFirstChar { it.uppercase(locale) }
    }

    // Fungsi memuat jadwal
    fun loadJadwals() {
        scope.launch {
            isLoading = true
            errorMessage = null
            val result = repository.getJadwals(hariIni)
            result.onSuccess { response ->
                jadwals = response.data
                isLoading = false
            }.onFailure { exception ->
                errorMessage = exception.message
                isLoading = false
                if (exception.message?.contains("Sesi Anda telah berakhir") == true || exception.message?.contains(
                        "Token tidak ditemukan"
                    ) == true
                ) {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }

    // Fungsi memuat tugas
    fun loadTugas() {
        scope.launch {
            isLoadingTugas = true
            errorMessageTugas = null
            val result = tugasRepository.getTugass("belum selesai")
            result.onSuccess { response ->
                tugasList = response.data
                isLoadingTugas = false
            }.onFailure { exception ->
                errorMessageTugas = exception.message
                isLoadingTugas = false

                if (exception.message?.contains("Sesi Anda telah berakhir") == true || exception.message?.contains(
                        "Token tidak ditemukan"
                    ) == true
                ) {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }

    // Fungsi memuat transaksi
    fun loadTransaksi() {
        scope.launch {
            isLoadingTransaksi = true
            errorMessageTransaksi = null
            val result = transaksiRepository.getTransaksis(
                bulan = currentMonth.toString(),
                tahun = currentYear.toString()
            )
            result.onSuccess { response ->
                rekap = response.data
                isLoadingTransaksi = false
            }.onFailure { exception ->
                errorMessageTransaksi = exception.message
                isLoadingTransaksi = false

                if (exception.message?.contains("Sesi Anda telah berakhir") == true || exception.message?.contains(
                        "Token tidak ditemukan"
                    ) == true
                ) {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
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
                    result.onSuccess {
                        showEditDialog = false
                        selectedTugas = null
                        loadTugas()
                        isLoading = false
                    }.onFailure { exception ->
                        errorMessage = exception.message
                        isLoading = false
                    }
                }
            }
        )
    }


    // Load data pertama kali
    LaunchedEffect(Unit) {
        loadJadwals()
        loadTugas()
        loadTransaksi()
    }

    ModalNavigationDrawer(
        drawerState = drawerState, drawerContent = {
            AppDrawer(
                navController = navController,
                onItemSelected = { scope.launch { drawerState.close() } })
        }) {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = Color.Black,
                        navigationIconContentColor = Color.Black
                    ), title = {
                        Text(
                            text = "",
                        )
                    }, navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }, windowInsets = WindowInsets(0, 0, 0, 0)
                )
            }, containerColor = Color.White, contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { padding ->
            when {
                isLoading || isLoadingTugas || isLoadingTransaksi -> {
                    Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF8A93D7))
                    }
                }

                errorMessage != null || errorMessageTugas != null || errorMessageTransaksi != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = errorMessage ?: errorMessageTugas ?: errorMessageTransaksi
                                ?: "Terjadi kesalahan",
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    loadJadwals()
                                    loadTugas()
                                    loadTransaksi()
                                }, colors = ButtonDefaults.buttonColors(
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
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {

                        item {
                            JadwalSection(jadwals = jadwals)
                        }

                        item {
                            TimelineSection(
                                title = "Timeline",
                                tugasList = tugasList, modifier = Modifier.fillMaxWidth(),

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
                                            loadTugas()
                                        }.onFailure { exception ->
                                            errorMessage = exception.message
                                            isLoading = false
                                        }
                                    }
                                },
                            )
                        }

                        item {
                            SaldoSection(
                                navController = navController,
                                modifier = Modifier.fillMaxWidth(),
                                saldo = rekap?.saldo ?: "0",
                                pemasukan = rekap?.pemasukan ?: "0",
                                pengeluaran = rekap?.pengeluaran ?: "0",
                                selisih = rekap?.selisih ?: "0"
                            )
                        }
                    }
                }
            }
        }
    }
}
//
//fun scheduleDailyReminder(context: android.content.Context) {
//    val workManager = WorkManager.getInstance(context)
//
//    val reminderRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(
//        1, TimeUnit.DAYS
//    )
//        .setInitialDelay(calculateInitialDelay(14, 58), TimeUnit.MILLISECONDS) // jam 8 pagi
//        .build()
//
//    workManager.enqueueUniquePeriodicWork(
//        "daily_reminder",
//        ExistingPeriodicWorkPolicy.UPDATE,
//        reminderRequest
//    )
//}

// Hitung delay sampai jam tertentu
fun calculateInitialDelay(hour: Int, minute: Int): Long {
    val current = Calendar.getInstance()
    val due = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        if (before(current)) add(Calendar.DAY_OF_MONTH, 1)
    }
    return due.timeInMillis - current.timeInMillis
}
