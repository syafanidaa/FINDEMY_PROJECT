package com.example.findemy.ui.calendar.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.findemy.data.model.Event
import com.example.findemy.data.model.EventRequest
import com.example.findemy.ui.components.CustomTimePickerDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventDialog(
    event: Event,
    onDismiss: () -> Unit,
    onConfirm: (EventRequest) -> Unit
) {
    // Parse existing data dari event
    val sdfDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val sdfTime = SimpleDateFormat("HH:mm", Locale.getDefault())

    // Extract tanggal dan jam dari event
    val (initialTanggalMulai, initialJamMulai) = try {
        val dateMulai = sdfDateTime.parse(event.tanggal_mulai)
        val tanggal = sdfDate.format(dateMulai!!)
        val jam = sdfTime.format(dateMulai)
        tanggal to jam
    } catch (e: Exception) {
        "" to ""
    }

    val (initialTanggalSelesai, initialJamSelesai) = try {
        val dateSelesai = sdfDateTime.parse(event.tanggal_selesai)
        val tanggal = sdfDate.format(dateSelesai!!)
        val jam = sdfTime.format(dateSelesai)
        tanggal to jam
    } catch (e: Exception) {
        "" to ""
    }

    var judul by remember { mutableStateOf(event.judul) }
    var tanggalMulai by remember { mutableStateOf(initialTanggalMulai) }
    var tanggalSelesai by remember { mutableStateOf(initialTanggalSelesai) }
    var jamMulai by remember { mutableStateOf(initialJamMulai) }
    var jamSelesai by remember { mutableStateOf(initialJamSelesai) }
    var enableNotifikasi by remember { mutableStateOf(event.pasang_pengingat) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Time Picker States
    var showJamMulaiPicker by remember { mutableStateOf(false) }
    var showJamSelesaiPicker by remember { mutableStateOf(false) }
    val jamMulaiState = rememberTimePickerState(is24Hour = true)
    val jamSelesaiState = rememberTimePickerState(is24Hour = true)


    // Date Picker States
    var showTanggalMulaiPicker by remember { mutableStateOf(false) }
    var showTanggalSelesaiPicker by remember { mutableStateOf(false) }

    // Set batas minimal tanggal = hari ini (00:00:00)
    val today = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    val tanggalMulaiState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= today
            }
        }
    )
    val tanggalSelesaiState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= today
            }
        }
    )

    val context = LocalContext.current

    // Date Picker Tanggal Mulai
    if (showTanggalMulaiPicker) {
        DatePickerDialog(
            onDismissRequest = { showTanggalMulaiPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        tanggalMulaiState.selectedDateMillis?.let { millis ->
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            tanggalMulai = sdf.format(Date(millis))
                        }
                        showTanggalMulaiPicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTanggalMulaiPicker = false }) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(state = tanggalMulaiState)
        }
    }

    // Date Picker Tanggal Selesai
    if (showTanggalSelesaiPicker) {
        DatePickerDialog(
            onDismissRequest = { showTanggalSelesaiPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        tanggalSelesaiState.selectedDateMillis?.let { millis ->
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            tanggalSelesai = sdf.format(Date(millis))
                        }
                        showTanggalSelesaiPicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTanggalSelesaiPicker = false }) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(state = tanggalSelesaiState)
        }
    }

    // Time Picker Jam Mulai
    if (showJamMulaiPicker) {
        CustomTimePickerDialog(
            onDismissRequest = { showJamMulaiPicker = false },
            onConfirm = {
                jamMulai = String.format(
                    Locale.getDefault(),
                    "%02d:%02d",
                    jamMulaiState.hour,
                    jamMulaiState.minute
                )
                showJamMulaiPicker = false
            }
        ) {
            TimePicker(state = jamMulaiState)
        }
    }

    // Time Picker Jam Selesai
    if (showJamSelesaiPicker) {
        CustomTimePickerDialog(
            onDismissRequest = { showJamSelesaiPicker = false },
            onConfirm = {
                jamSelesai = String.format(
                    Locale.getDefault(),
                    "%02d:%02d",
                    jamSelesaiState.hour,
                    jamSelesaiState.minute
                )
                showJamSelesaiPicker = false
            }
        ) {
            TimePicker(state = jamSelesaiState)
        }
    }

    // Main Dialog
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Edit Event",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Input Judul
                OutlinedTextField(
                    value = judul,
                    onValueChange = { judul = it },
                    label = { Text("Nama Acara") },
                    placeholder = { Text("Contoh: Webinar Kebangsaan") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = judul.isBlank() && errorMessage != null
                )

                // Row untuk Tanggal Mulai dan Tanggal Selesai
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Tanggal Mulai
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showTanggalMulaiPicker = true }
                    ) {
                        OutlinedTextField(
                            value = tanggalMulai,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tanggal Mulai") },
                            placeholder = { Text("2024-01-15") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = if (tanggalMulai.isBlank() && errorMessage != null)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.outline,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            singleLine = true
                        )
                    }

                    // Tanggal Selesai
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showTanggalSelesaiPicker = true }
                    ) {
                        OutlinedTextField(
                            value = tanggalSelesai,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tanggal Selesai") },
                            placeholder = { Text("2024-01-15") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = if (tanggalSelesai.isBlank() && errorMessage != null)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.outline,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            singleLine = true
                        )
                    }
                }

                // Row untuk Jam Mulai dan Jam Selesai
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Jam Mulai
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showJamMulaiPicker = true }
                    ) {
                        OutlinedTextField(
                            value = jamMulai,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Jam Mulai") },
                            placeholder = { Text("08:00") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = if (jamMulai.isBlank() && errorMessage != null)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.outline,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            singleLine = true
                        )
                    }

                    // Jam Selesai
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showJamSelesaiPicker = true }
                    ) {
                        OutlinedTextField(
                            value = jamSelesai,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Jam Selesai") },
                            placeholder = { Text("10:00") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = if (jamSelesai.isBlank() && errorMessage != null)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.outline,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            singleLine = true
                        )
                    }
                }

                // Pasang Pengingat
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Pasang Pengingat",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Switch(
                            checked = enableNotifikasi,
                            onCheckedChange = { enableNotifikasi = it }
                        )
                    }
                }

                // Error Message (jika ada)
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Reset error message
                    errorMessage = null

                    // Validasi 1: Semua field terisi
                    if (judul.isBlank() || tanggalMulai.isBlank() || tanggalSelesai.isBlank() ||
                        jamMulai.isBlank() || jamSelesai.isBlank()
                    ) {
                        errorMessage = "Semua field harus diisi"
                        return@Button
                    }

                    // Validasi 2: Tanggal selesai tidak boleh sebelum tanggal mulai
                    try {
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val dateMulai = sdf.parse(tanggalMulai)
                        val dateSelesai = sdf.parse(tanggalSelesai)

                        if (dateMulai != null && dateSelesai != null) {
                            // Cek apakah tanggal selesai sebelum tanggal mulai
                            if (dateSelesai.before(dateMulai)) {
                                errorMessage = "Tanggal selesai tidak boleh sebelum tanggal mulai"
                                return@Button
                            }

                            // Validasi 3: Jika tanggal sama, cek jam
                            if (tanggalMulai == tanggalSelesai) {
                                // Parse jam
                                val timeMulaiParts = jamMulai.split(":")
                                val timeSelesaiParts = jamSelesai.split(":")

                                val hourMulai = timeMulaiParts[0].toIntOrNull() ?: 0
                                val minuteMulai = timeMulaiParts[1].toIntOrNull() ?: 0
                                val hourSelesai = timeSelesaiParts[0].toIntOrNull() ?: 0
                                val minuteSelesai = timeSelesaiParts[1].toIntOrNull() ?: 0

                                // Konversi ke menit untuk perbandingan mudah
                                val totalMinutesMulai = hourMulai * 60 + minuteMulai
                                val totalMinutesSelesai = hourSelesai * 60 + minuteSelesai

                                // Cek apakah jam selesai sebelum atau sama dengan jam mulai
                                if (totalMinutesSelesai <= totalMinutesMulai) {
                                    errorMessage = "Jam selesai harus lebih besar dari jam mulai"
                                    return@Button
                                }

                                // Validasi 4: Durasi minimal (opsional, misal minimal 30 menit)
                                val durasiMenit = totalMinutesSelesai - totalMinutesMulai
                                if (durasiMenit < 30) {
                                    errorMessage = "Durasi acara minimal 30 menit"
                                    return@Button
                                }
                            }
                        }
                    } catch (e: Exception) {
                        errorMessage = "Format tanggal atau jam tidak valid"
                        return@Button
                    }

                    // Semua validasi lolos, buat request
                    val request = EventRequest(
                        judul = judul.trim(),
                        tanggal_mulai = "$tanggalMulai $jamMulai:00",
                        tanggal_selesai = "$tanggalSelesai $jamSelesai:00" ,
                        pasang_pengingat = enableNotifikasi

                    )

                    // Kirim request
                    onConfirm(request)
                },
                enabled = judul.isNotBlank() &&
                        tanggalMulai.isNotBlank() &&
                        tanggalSelesai.isNotBlank() &&
                        jamMulai.isNotBlank() &&
                        jamSelesai.isNotBlank()
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}