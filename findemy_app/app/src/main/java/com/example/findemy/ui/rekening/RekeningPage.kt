package com.example.findemy.ui.rekening

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.findemy.R
import com.example.findemy.data.model.Rekening
import com.example.findemy.data.model.RekeningRequest
import com.example.findemy.data.repository.RekeningRepository
import com.example.findemy.ui.rekening.components.AddRekeningDialog
import com.example.findemy.ui.rekening.components.EditRekeningDialog
import com.example.findemy.ui.rekening.components.RekeningCard
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RekeningPage(navController: NavController) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var rekeningList by remember { mutableStateOf<List<Rekening>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedRekening by remember { mutableStateOf<Rekening?>(null) }
    var showSuccessSnackbar by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    val repository = remember { RekeningRepository(context) }
    val snackbarHostState = remember { SnackbarHostState() }

    fun loadRekening() {
        scope.launch {
            isLoading = true
            errorMessage = null
            val result = repository.getRekenings()
            result.onSuccess { response ->
                rekeningList = response.data
                isLoading = false
            }.onFailure { exception ->
                errorMessage = exception.message
                isLoading = false

                if (exception.message?.contains("Sesi Anda telah berakhir") == true ||
                    exception.message?.contains("Token tidak ditemukan") == true
                ) {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
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

    // Dialog tambah rekening
    if (showAddDialog) {
        AddRekeningDialog (
            onDismiss = { showAddDialog = false },
            onConfirm = { nama, saldo ->
                scope.launch {
                    isLoading = true
                    val result = repository.createRekening(RekeningRequest(nama, saldo))
                    result.onSuccess {
                        showAddDialog = false
                        successMessage = "Rekening berhasil ditambahkan"
                        showSuccessSnackbar = true
                        loadRekening()
                    }.onFailure { exception ->
                        errorMessage = exception.message
                        isLoading = false
                    }
                }
            }
        )
    }

    // Dialog edit rekening
    if (showEditDialog && selectedRekening != null) {
        EditRekeningDialog (
            rekening = selectedRekening!!,
            onDismiss = {
                showEditDialog = false
                selectedRekening = null
            },
            onConfirm = { nama, saldo ->
                scope.launch {
                    isLoading = true
                    val result = repository.updateRekening(selectedRekening!!.id, RekeningRequest(nama, saldo))
                    result.onSuccess {
                        showEditDialog = false
                        selectedRekening = null
                        successMessage = "Rekening berhasil diperbarui"
                        showSuccessSnackbar = true
                        loadRekening()
                    }.onFailure { exception ->
                        errorMessage = exception.message
                        isLoading = false
                    }
                }
            }
        )
    }

    // Dialog konfirmasi hapus
    if (showDeleteDialog && selectedRekening != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                selectedRekening = null
            },
            title = { Text("Hapus Rekening") },
            text = { Text("Apakah Anda yakin ingin menghapus rekening ${selectedRekening!!.nama}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            val result = repository.deleteRekening(selectedRekening!!.id)
                            result.onSuccess {
                                showDeleteDialog = false
                                selectedRekening = null
                                successMessage = "Rekening berhasil dihapus"
                                showSuccessSnackbar = true
                                loadRekening()
                            }.onFailure { exception ->
                                errorMessage = exception.message
                                isLoading = false
                            }
                        }
                    }
                ) {
                    Text("Hapus", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        selectedRekening = null
                    }
                ) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Rekening",
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
        },
        containerColor = Color.White,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF079CD2),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Rekening")
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
                        Button(onClick = { loadRekening() }) {
                            Text("Coba Lagi")
                        }
                    }
                }
            }

            rekeningList.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.empty_maskot),
                            contentDescription = "Empty state",
                            modifier = Modifier
                                .size(180.dp)
                                .padding(bottom = 16.dp)
                        )
                        Text(
                            text = "Belum ada rekening",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 100.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(rekeningList) { rekening ->
                        RekeningCard(
                            rekening = rekening,
                            onEdit = {
                                selectedRekening = rekening
                                showEditDialog = true
                            },
                            onDelete = {
                                selectedRekening = rekening
                                showDeleteDialog = true
                            }
                        )
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