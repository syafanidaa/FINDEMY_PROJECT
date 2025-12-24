package com.example.findemy.ui.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.findemy.data.model.RekapTransaksi
import com.example.findemy.data.model.Rekening
import com.example.findemy.data.model.Transaksi
import com.example.findemy.data.model.TransaksiRequest
import com.example.findemy.data.repository.RekeningRepository
import com.example.findemy.data.repository.TransaksiRepository
import com.example.findemy.ui.components.AppDrawer
import com.example.findemy.ui.transaction.components.AddTransactionDialog
import com.example.findemy.ui.transaction.components.MonthYearFilter
import com.example.findemy.ui.transaction.components.SaldoSection
import com.example.findemy.ui.transaction.components.TransactionCard
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionPage(navController: NavController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var rekap by remember { mutableStateOf<RekapTransaksi?>(null) }
    var transaksiList by remember { mutableStateOf<List<Transaksi>>(emptyList()) }
    var rekeningList by remember { mutableStateOf<List<Rekening>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showSuccessSnackbar by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    val calendar = remember { Calendar.getInstance() }
    var currentMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH) + 1) }
    var currentYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }

    val transaksiRepository = remember { TransaksiRepository(context) }
    val rekeningRepository = remember { RekeningRepository(context) }
    val snackbarHostState = remember { SnackbarHostState() }

    fun loadTransaksi() {
        scope.launch {
            isLoading = true
            errorMessage = null
            val result = transaksiRepository.getTransaksis(
                bulan = currentMonth.toString(), tahun = currentYear.toString()
            )
            result.onSuccess { response ->
                rekap = response.data
                transaksiList = response.data.transaksi
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

    fun loadRekening() {
        scope.launch {
            val result = rekeningRepository.getRekenings()
            result.onSuccess { response ->
                rekeningList = response.data
            }
        }
    }

    fun previousMonth() {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, currentYear)
            set(Calendar.MONTH, currentMonth - 1)
            add(Calendar.MONTH, -1)
        }
        currentMonth = cal.get(Calendar.MONTH) + 1
        currentYear = cal.get(Calendar.YEAR)
        loadTransaksi()
    }

    fun nextMonth() {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, currentYear)
            set(Calendar.MONTH, currentMonth - 1)
            add(Calendar.MONTH, 1)
        }
        currentMonth = cal.get(Calendar.MONTH) + 1
        currentYear = cal.get(Calendar.YEAR)
        loadTransaksi()
    }

    LaunchedEffect(Unit) {
        loadTransaksi()
        loadRekening()
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

    // Dialog tambah transaksi
    if (showAddDialog) {
        AddTransactionDialog(
            rekeningList = rekeningList.map { it.id to it.nama },
            onDismiss = { showAddDialog = false },
            onConfirm = { rekeningId, jenis, keterangan, jumlah ->
                scope.launch {
                    isLoading = true
                    val result = transaksiRepository.createTransaksi(
                        TransaksiRequest(rekeningId, jenis, keterangan, jumlah)
                    )
                    result.onSuccess {
                        showAddDialog = false
                        successMessage = "Transaksi berhasil ditambahkan"
                        showSuccessSnackbar = true
                        loadTransaksi()
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
                onItemSelected = { scope.launch { drawerState.close() } })
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
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = Color(0xFF079CD2),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Transaksi")
                }
            }
        ) { padding ->
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF079CD2))
                    }
                }

                errorMessage != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
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
                            Button(onClick = { loadTransaksi() }) {
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
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 16.dp,
                            bottom = 100.dp
                        )
                    ) {
                        // Month/Year Filter
                        item {
                            MonthYearFilter(
                                currentMonth = rekap?.bulan ?: "",
                                currentYear = rekap?.tahun ?: "",
                                onPrevious = { previousMonth() },
                                onNext = { nextMonth() })
                        }

                        // Saldo Section
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

                        // Transaction List Header
                        item {
                            Text(
                                text = "Riwayat Transaksi",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        // Transaction Items
                        if (transaksiList.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Belum ada transaksi",
                                        color = Color.Gray
                                    )
                                }
                            }
                        } else {
                            items(transaksiList) { transaksi ->
                                TransactionCard(transaksi = transaksi)
                            }
                        }
                    }
                }
            }
        }
    }
}


fun formatCurrency(value: String): String {
    return try {
        val number = value.toLongOrNull() ?: 0
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        format.format(number)
    } catch (e: Exception) {
        "Rp 0"
    }
}

fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}