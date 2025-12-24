package com.example.findemy.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.findemy.data.model.Jadwal
import com.example.findemy.data.repository.JadwalRepository
import com.example.findemy.ui.components.AppDrawer
import kotlinx.coroutines.launch
import com.example.findemy.R
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.findemy.data.repository.TugasRepository
import com.example.findemy.ui.notification.NotificationScheduler
import com.example.findemy.ui.schedule.components.AddJadwalDialog
import com.example.findemy.ui.schedule.components.AddTugasDialog
import com.example.findemy.ui.schedule.components.EditJadwalDialog
import com.example.findemy.ui.schedule.components.JadwalCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedulePage(navController: NavController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var jadwals by remember { mutableStateOf<List<Jadwal>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedTabIndex by remember { mutableStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showAddTugasDialog by remember { mutableStateOf(false) }
    var selectedJadwalForTugas by remember { mutableStateOf<Jadwal?>(null) }
    var selectedJadwal by remember { mutableStateOf<Jadwal?>(null) }
    var showSuccessSnackbar by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    val repository = remember { JadwalRepository(context) }
    val taskRepository = remember { TugasRepository(context) }
    val snackbarHostState = remember { SnackbarHostState() }
    val hariList = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")

    fun loadJadwals() {
        scope.launch {
            isLoading = true
            errorMessage = null
            val result = repository.getJadwals()
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

    LaunchedEffect(Unit) {
        loadJadwals()
    }

    // Show success snackbar
    LaunchedEffect(showSuccessSnackbar) {
        if (showSuccessSnackbar) {
            snackbarHostState.showSnackbar(
                message = successMessage, duration = SnackbarDuration.Short
            )
            showSuccessSnackbar = false
        }
    }
// Dialog tambah jadwal
    if (showAddDialog) {
        AddJadwalDialog(onDismiss = { showAddDialog = false }, onConfirm = { request ->
            scope.launch {
                isLoading = true
                val result = repository.createJadwal(request)
                result.onSuccess { response ->
                    showAddDialog = false
                    successMessage = "Jadwal berhasil ditambahkan"
                    showSuccessSnackbar = true

                    if (response.pasang_pengingat) {
                        NotificationScheduler.scheduleJadwalNotification(context, response)
                    }

                    loadJadwals()
                }.onFailure { exception ->
                    errorMessage = exception.message
                    isLoading = false
                }
            }
        })
    }

// Dialog edit jadwal
    if (showEditDialog && selectedJadwal != null) {
        EditJadwalDialog(jadwal = selectedJadwal!!, onDismiss = {
            showEditDialog = false
            selectedJadwal = null
        }, onConfirm = { request ->
            scope.launch {
                isLoading = true
                val result = repository.updateJadwal(selectedJadwal!!.id, request)
                result.onSuccess { response ->
                    showEditDialog = false
                    val jadwalId = selectedJadwal!!.id
                    selectedJadwal = null
                    successMessage = "Jadwal berhasil diperbarui"
                    showSuccessSnackbar = true

                    NotificationScheduler.cancelNotification(context, "jadwal", jadwalId)

                    if (response.pasang_pengingat) {
                        NotificationScheduler.scheduleJadwalNotification(context, response)
                    }

                    loadJadwals()
                }.onFailure { exception ->
                    errorMessage = exception.message
                    isLoading = false
                }
            }
        })
    }

    if (showAddTugasDialog && selectedJadwalForTugas != null) {
        AddTugasDialog(jadwalId = selectedJadwalForTugas!!.id, onDismiss = {
            showAddTugasDialog = false
            selectedJadwalForTugas = null
        }, onConfirm = { tugasRequest ->
            scope.launch {
                isLoading = true
                // Ganti dengan repository Anda
                val result = taskRepository.createTugas(tugasRequest)
                result.onSuccess { response ->
                    successMessage = "Tugas berhasil ditambahkan"
                    showSuccessSnackbar = true
                    showAddTugasDialog = false
                    selectedJadwalForTugas = null
                    isLoading = false

                    if (response.pasang_pengingat) {
                        NotificationScheduler.scheduleTugasNotification(context, response)
                    }
                }.onFailure { exception ->
                    errorMessage = exception.message
                    isLoading = false
                }
            }
        })
    }


    ModalNavigationDrawer(
        drawerState = drawerState, drawerContent = {
            AppDrawer(
                navController = navController,
                onItemSelected = { scope.launch { drawerState.close() } })
        }) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
            },
            containerColor = Color.White,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = Color(0xFF079CD2),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Jadwal")
                }
            }) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {

                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.White,
                    contentColor = Color.Black,
                    edgePadding = 16.dp,
                    indicator = {},
                    divider = {}) {
                    hariList.forEachIndexed { index, hari ->
                        val isSelected = selectedTabIndex == index

                        Tab(
                            selected = isSelected,
                            onClick = { selectedTabIndex = index },
                            modifier = Modifier
                                .padding(horizontal = 4.dp, vertical = 12.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .then(
                                    if (isSelected) {
                                        Modifier.background(Color(0xFF079CD2))
                                    } else {
                                        Modifier.border(
                                                width = 1.dp,
                                                color = Color(0xFFE0E0E0),
                                                shape = RoundedCornerShape(8.dp)
                                            ).background(Color.Transparent)
                                    }
                                )
                                .height(32.dp),
                            text = {
                                Text(
                                    text = hari,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                                    color = if (isSelected) Color.White else Color(0xFF9E9E9E)
                                )
                            })
                    }
                }

                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF8A93D7))
                        }
                    }

                    errorMessage != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
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
                                Button(onClick = { loadJadwals() }) {
                                    Text("Coba Lagi")
                                }
                            }
                        }
                    }

                    else -> {
                        val selectedHari = hariList[selectedTabIndex]
                        val filteredJadwals =
                            jadwals.filter { it.hari == selectedHari }.sortedBy { it.jam_mulai }

                        if (filteredJadwals.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
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
                                        text = "Tidak ada jadwal untuk hari $selectedHari",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.Gray
                                    )
                                }
                            }

                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(0.dp)
                            ) {
                                items(filteredJadwals) { jadwal ->
                                    JadwalCard(jadwal = jadwal, onEdit = { selectedJadwalItem ->
                                        selectedJadwal = selectedJadwalItem
                                        showEditDialog = true
                                    }, onDelete = { selectedJadwalItem ->
                                        scope.launch {
                                            isLoading = true
                                            val result =
                                                repository.deleteJadwal(selectedJadwalItem.id)
                                            result.onSuccess {
                                                successMessage = "Jadwal berhasil dihapus"
                                                showSuccessSnackbar = true
                                                NotificationScheduler.cancelNotification(context, "jadwal", selectedJadwalItem.id)

                                                loadJadwals()
                                            }.onFailure { exception ->
                                                errorMessage = exception.message
                                                isLoading = false
                                            }
                                        }
                                    }, onAddTugas = { selectedJadwalItem ->
                                        selectedJadwalForTugas = selectedJadwalItem
                                        showAddTugasDialog = true
                                    })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}