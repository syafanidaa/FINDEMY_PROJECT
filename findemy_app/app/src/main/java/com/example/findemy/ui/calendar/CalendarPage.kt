package com.example.findemy.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.findemy.data.model.Event
import com.example.findemy.data.model.Jadwal
import com.example.findemy.data.repository.EventRepository
import com.example.findemy.ui.calendar.components.AddEventDialog
import com.example.findemy.ui.calendar.components.EditEventDialog
import com.example.findemy.ui.calendar.components.EventCard
import com.example.findemy.ui.components.AppDrawer
import com.example.findemy.ui.notification.NotificationScheduler
import com.example.findemy.ui.schedule.components.AddJadwalDialog
import com.example.findemy.ui.schedule.components.EditJadwalDialog
import com.example.findemy.ui.schedule.components.JadwalCard
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarPage(navController: NavController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedEvent by remember { mutableStateOf<Event?>(null) }
    var showSuccessSnackbar by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    val repository = remember { EventRepository(context) }

    // Filter events untuk tanggal terpilih
    val selectedDateEvents = remember(events, selectedDate) {
        events.filter { event ->
            val eventDate = LocalDateTime.parse(
                event.tanggal_mulai,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            ).toLocalDate()

            val eventEndDate = LocalDateTime.parse(
                event.tanggal_selesai,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            ).toLocalDate()

            // Cek apakah selectedDate ada di range event
            selectedDate >= eventDate && selectedDate <= eventEndDate
        }
    }

    // Set tanggal yang memiliki event
    val datesWithEvents = remember(events) {
        events.flatMap { event ->
            val startDate = LocalDateTime.parse(
                event.tanggal_mulai,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            ).toLocalDate()

            val endDate = LocalDateTime.parse(
                event.tanggal_selesai,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            ).toLocalDate()

            // Generate semua tanggal dari start ke end
            generateSequence(startDate) { date ->
                if (date < endDate) date.plusDays(1) else null
            }.toList() + endDate
        }.toSet()
    }

    fun loadEvents() {
        scope.launch {
            isLoading = true
            errorMessage = null
            val result = repository.getEvents()
            result.onSuccess { response ->
                events = response.data
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
        loadEvents()
    }

    // Dialog tambah event
    if (showAddDialog) {
        AddEventDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { request ->
                scope.launch {
                    isLoading = true
                    val result = repository.createEvent(request)
                    result.onSuccess { response ->
                        showAddDialog = false
                        successMessage = "Event berhasil ditambahkan"
                        showSuccessSnackbar = true

                        if (response.pasang_pengingat) {
                            NotificationScheduler.scheduleEventNotification(context, response)
                        }
                        loadEvents()
                    }.onFailure { exception ->
                        errorMessage = exception.message
                        isLoading = false
                    }
                }
            }
        )
    }

    // Dialog edit event
    if (showEditDialog && selectedEvent != null) {
        EditEventDialog(
            event = selectedEvent!!,
            onDismiss = {
                showEditDialog = false
                selectedEvent = null
            },
            onConfirm = { request ->
                scope.launch {
                    isLoading = true
                    val result = repository.updateJadwal(selectedEvent!!.id, request)
                    result.onSuccess { response ->
                        showEditDialog = false
                        selectedEvent = null
                        successMessage = "Event berhasil diperbarui"
                        showSuccessSnackbar = true

                        NotificationScheduler.cancelNotification(context, "event", response.id)

                        if (response.pasang_pengingat) {
                            NotificationScheduler.scheduleEventNotification(context, response)
                        }

                        loadEvents()
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
            }, containerColor = Color.White, contentWindowInsets = WindowInsets(0, 0, 0, 0),

        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
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
                                Button(onClick = { loadEvents() }) {
                                    Text("Coba Lagi")
                                }
                            }
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Header dengan tanggal terpilih
                            item {
                                SelectedDateHeader(selectedDate, selectedDateEvents.size)
                            }

                            // Kontrol bulan
                            item {
                                MonthControl(
                                    currentMonth = currentMonth,
                                    onPreviousMonth = {
                                        currentMonth = currentMonth.minusMonths(1)
                                    },
                                    onNextMonth = { currentMonth = currentMonth.plusMonths(1) }
                                )
                            }

                            // Grid kalender
                            item {
                                CalendarGrid(
                                    currentMonth = currentMonth,
                                    selectedDate = selectedDate,
                                    datesWithEvents = datesWithEvents,
                                    onDateSelected = { selectedDate = it }
                                )
                            }

                            // Divider
                            // Row dengan tombol di kanan
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp, horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    HorizontalDivider(
                                        modifier = Modifier.weight(1f),
                                        color = Color.LightGray
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            showAddDialog = true
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF079CD2),
                                            contentColor = Color.White
                                        ),
                                        shape = CircleShape, // membuat tombol bulat
                                        contentPadding = PaddingValues(12.dp) // atur agar tidak terlalu besar
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Tambah Event",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                }
                            }

                            // List events untuk tanggal terpilih
                            if (selectedDateEvents.isNotEmpty()) {
                                items(selectedDateEvents) { event ->
                                    EventCard (
                                        event = event,
                                        onEdit = { selectedEventItem ->
                                            selectedEvent = selectedEventItem
                                            showEditDialog = true
                                        },
                                        onDelete = { selectedEventItem ->
                                            scope.launch {
                                                isLoading = true
                                                val result =
                                                    repository.deleteEvent(selectedEventItem.id)
                                                result.onSuccess {
                                                    successMessage = "Event berhasil dihapus"
                                                    showSuccessSnackbar = true
                                                    NotificationScheduler.cancelNotification(context, "event", selectedEventItem.id)
                                                    loadEvents()
                                                }.onFailure { exception ->
                                                    errorMessage = exception.message
                                                    isLoading = false
                                                }
                                            }
                                        }
                                    )
                                }
                            } else {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Event,
                                                contentDescription = null,
                                                tint = Color.LightGray,
                                                modifier = Modifier.size(64.dp)
                                            )
                                            Spacer(modifier = Modifier.height(16.dp))
                                            Text(
                                                text = "Tidak ada event pada tanggal ini",
                                                color = Color.Gray,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            }

                            // Spacer untuk FAB
                            item {
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SelectedDateHeader(date: LocalDate, eventCount: Int) {
    val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("id", "ID"))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = String.format("%02d", date.dayOfMonth),
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )
        Column {

            Text(
                text = dayOfWeek.replaceFirstChar { it.uppercase() },
                fontSize = 16.sp,
                color = Color.Gray
            )
            Text(
                text = "${
                    date.month.getDisplayName(
                        TextStyle.FULL,
                        Locale("id", "ID")
                    )
                } ${date.year}",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun MonthControl(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "Bulan Sebelumnya"
            )
        }

        Text(
            text = "${
                currentMonth.month.getDisplayName(
                    TextStyle.FULL,
                    Locale("id", "ID")
                )
            } ${currentMonth.year}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )

        IconButton(onClick = onNextMonth) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Bulan Berikutnya"
            )
        }
    }
}

@Composable
fun CalendarGrid(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    datesWithEvents: Set<LocalDate>,
    onDateSelected: (LocalDate) -> Unit
) {
    val daysOfWeek = listOf("M", "T", "W", "T", "F", "S", "S")
    val today = LocalDate.now()

    Column(modifier = Modifier.fillMaxWidth()) {
        // Header hari
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Grid tanggal
        val firstDayOfMonth = currentMonth.atDay(1)
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value
        val daysInMonth = currentMonth.lengthOfMonth()
        val previousMonth = currentMonth.minusMonths(1)
        val daysInPreviousMonth = previousMonth.lengthOfMonth()

        val daysFromPrevMonth = firstDayOfWeek - 1

        var dayCounter = 1
        var nextMonthCounter = 1

        for (week in 0..5) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (dayOfWeek in 1..7) {
                    val cellIndex = week * 7 + dayOfWeek

                    when {
                        cellIndex <= daysFromPrevMonth -> {
                            val day = daysInPreviousMonth - daysFromPrevMonth + cellIndex
                            CalendarDay(
                                day = day,
                                isCurrentMonth = false,
                                isSelected = false,
                                isToday = false,
                                hasEvent = false,
                                onClick = { }
                            )
                        }

                        dayCounter <= daysInMonth -> {
                            val date = currentMonth.atDay(dayCounter)
                            CalendarDay(
                                day = dayCounter,
                                isCurrentMonth = true,
                                isSelected = date == selectedDate,
                                isToday = date == today,
                                hasEvent = datesWithEvents.contains(date),
                                onClick = { onDateSelected(date) }
                            )
                            dayCounter++
                        }

                        else -> {
                            CalendarDay(
                                day = nextMonthCounter,
                                isCurrentMonth = false,
                                isSelected = false,
                                isToday = false,
                                hasEvent = false,
                                onClick = { }
                            )
                            nextMonthCounter++
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarDay(
    day: Int,
    isCurrentMonth: Boolean,
    isSelected: Boolean,
    isToday: Boolean,
    hasEvent: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .padding(4.dp)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> Color(0xFF079CD2)
                    isToday -> Color(0xFF079CD2).copy(alpha = 0.3f)
                    else -> Color.Transparent
                }
            )
            .clickable(enabled = isCurrentMonth) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = day.toString(),
                color = when {
                    isSelected -> Color.White
                    !isCurrentMonth -> Color.LightGray
                    else -> Color.Black
                },
                fontSize = 14.sp,
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal
            )

            // Dot indicator untuk event
            if (hasEvent && isCurrentMonth) {
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF8A93D7))
                )
            }
        }
    }
}
//
//@Composable
//fun EventCard(event: Event) {
//    val startDateTime = LocalDateTime.parse(
//        event.tanggal_mulai,
//        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
//    )
//    val endDateTime = LocalDateTime.parse(
//        event.tanggal_selesai,
//        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
//    )
//
//    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
//
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        colors = CardDefaults.cardColors(
//            containerColor = Color(0xFF8A93D7)
//        ),
//        shape = RoundedCornerShape(12.dp),
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
//    ) {
//        Column(   modifier = Modifier
//            .fillMaxWidth()
//            .padding(12.dp)) {
//            Text(
//                text = event.judul,
//                color = Color.White,
//                fontSize = 16.sp,
//                fontWeight = FontWeight.Bold
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Row(
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Schedule,
//                    contentDescription = null,
//                    tint = Color.White,
//                    modifier = Modifier.size(16.dp)
//                )
//                Spacer(modifier = Modifier.width(4.dp))
//                Text(
//                    text = "${startDateTime.format(timeFormatter)} - ${
//                        endDateTime.format(
//                            timeFormatter
//                        )
//                    }",
//                    fontSize = 14.sp,
//                    color = Color.White,
//                )
//            }
//
//            if (startDateTime.toLocalDate() != endDateTime.toLocalDate()) {
//                Spacer(modifier = Modifier.height(4.dp))
//                val dateFormatter =
//                    DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("id", "ID"))
//                Text(
//                    text = "${startDateTime.format(dateFormatter)} - ${
//                        endDateTime.format(
//                            dateFormatter
//                        )
//                    }",
//                    fontSize = 12.sp,
//                    color = Color.White,
//                )
//            }
//        }
//
//    }
//}