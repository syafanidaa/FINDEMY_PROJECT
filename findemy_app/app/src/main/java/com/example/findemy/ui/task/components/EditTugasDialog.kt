package com.example.findemy.ui.task.components

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
import com.example.findemy.data.model.Tugas
import com.example.findemy.data.model.TugasRequest
import com.example.findemy.ui.components.CustomTimePickerDialog
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTugasDialog(
    tugas: Tugas,
    onDismiss: () -> Unit,
    onConfirm: (TugasRequest) -> Unit
) {
    // Parse deadline yang sudah ada
    val (initialTanggal, initialJam) = remember(tugas.deadline) {
        try {
            val parts = tugas.deadline.split(" ")
            if (parts.size >= 2) {
                Pair(parts[0], parts[1])
            } else {
                Pair("", "")
            }
        } catch (e: Exception) {
            Pair("", "")
        }
    }

    var judul by remember { mutableStateOf(tugas.judul) }
    var deskripsi by remember { mutableStateOf(tugas.deskripsi ?: "") }
    var tanggalDeadline by remember { mutableStateOf(initialTanggal) }
    var jamDeadline by remember { mutableStateOf(initialJam) }
    var status by remember { mutableStateOf(tugas.status) }
    var enableNotifikasi by remember { mutableStateOf(tugas.pasang_pengingat) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Time Picker State
    var showJamDeadlinePicker by remember { mutableStateOf(false) }

    // Parse initial time untuk TimePickerState
    val initialHour = remember(initialJam) {
        try {
            initialJam.split(":")[0].toIntOrNull() ?: 0
        } catch (e: Exception) {
            0
        }
    }
    val initialMinute = remember(initialJam) {
        try {
            initialJam.split(":")[1].toIntOrNull() ?: 0
        } catch (e: Exception) {
            0
        }
    }

    val jamDeadlineState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    // Date Picker State
    var showTanggalDeadlinePicker by remember { mutableStateOf(false) }

    // Set batas minimal tanggal = hari ini (00:00:00)
    val today = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    // Parse initial date untuk DatePickerState
    val initialDateMillis = remember(initialTanggal) {
        try {
            if (initialTanggal.isNotBlank()) {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                sdf.parse(initialTanggal)?.time
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    val tanggalDeadlineState = rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= today
            }
        }
    )

    val context = LocalContext.current

    // Date Picker Tanggal Deadline
    if (showTanggalDeadlinePicker) {
        DatePickerDialog(
            onDismissRequest = { showTanggalDeadlinePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        tanggalDeadlineState.selectedDateMillis?.let { millis ->
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            tanggalDeadline = sdf.format(Date(millis))
                        }
                        showTanggalDeadlinePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTanggalDeadlinePicker = false }) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(state = tanggalDeadlineState)
        }
    }

    // Time Picker Jam Deadline
    if (showJamDeadlinePicker) {
        CustomTimePickerDialog(
            onDismissRequest = { showJamDeadlinePicker = false },
            onConfirm = {
                jamDeadline = String.format(
                    Locale.getDefault(),
                    "%02d:%02d",
                    jamDeadlineState.hour,
                    jamDeadlineState.minute
                )
                showJamDeadlinePicker = false
            }
        ) {
            TimePicker(state = jamDeadlineState)
        }
    }

    // Main Dialog
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Edit Tugas",
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
                    label = { Text("Judul Tugas") },
                    placeholder = { Text("Contoh: Slicing UI UX") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = judul.isBlank() && errorMessage != null
                )

                // Input Deskripsi
                OutlinedTextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    label = { Text("Deskripsi") },
                    placeholder = { Text("Contoh: Membuat desain halaman login") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    isError = deskripsi.isBlank() && errorMessage != null
                )

                // Row untuk Tanggal dan Jam Deadline
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Tanggal Deadline
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showTanggalDeadlinePicker = true }
                    ) {
                        OutlinedTextField(
                            value = tanggalDeadline.ifEmpty { "" },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tanggal") },
                            placeholder = { Text("2025-10-28") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = if (tanggalDeadline.isBlank() && errorMessage != null)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.outline,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            singleLine = true
                        )
                    }

                    // Jam Deadline
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showJamDeadlinePicker = true }
                    ) {
                        OutlinedTextField(
                            value = jamDeadline.ifEmpty { "" },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Jam") },
                            placeholder = { Text("23:59") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = if (jamDeadline.isBlank() && errorMessage != null)
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

                // Dropdown Status
                var expandedStatus by remember { mutableStateOf(false) }
                val statusOptions = listOf("belum selesai", "selesai")

                ExposedDropdownMenuBox(
                    expanded = expandedStatus,
                    onExpandedChange = { expandedStatus = !expandedStatus }
                ) {
                    OutlinedTextField(
                        value = status,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Status") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatus)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedStatus,
                        onDismissRequest = { expandedStatus = false }
                    ) {
                        statusOptions.forEach { statusOption ->
                            DropdownMenuItem(
                                text = { Text(statusOption.replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    status = statusOption
                                    expandedStatus = false
                                }
                            )
                        }
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

                    // Validasi: Semua field terisi
                    if (judul.isBlank() || deskripsi.isBlank() ||
                        tanggalDeadline.isBlank() || jamDeadline.isBlank()
                    ) {
                        errorMessage = "Semua field harus diisi"
                        return@Button
                    }

                    // Gabungkan tanggal dan jam menjadi format "2025-10-28 23:59"
                    val deadline = "$tanggalDeadline $jamDeadline"

                    // Buat request
                    val request = TugasRequest(
                        jadwal_id = tugas.jadwal.id,
                        judul = judul.trim(),
                        deskripsi = deskripsi.trim(),
                        deadline = deadline,
                        status = status,
                        pasang_pengingat = enableNotifikasi
                    )

                    // Kirim request
                    onConfirm(request)
                },
                enabled = judul.isNotBlank() &&
                        deskripsi.isNotBlank() &&
                        tanggalDeadline.isNotBlank() &&
                        jamDeadline.isNotBlank()
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