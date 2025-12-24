package com.example.findemy.ui.schedule.components

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
import com.example.findemy.data.model.JadwalRequest
import com.example.findemy.ui.components.CustomTimePickerDialog
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddJadwalDialog(
    onDismiss: () -> Unit, onConfirm: (JadwalRequest) -> Unit
) {
    var mataKuliah by remember { mutableStateOf("") }
    var dosen by remember { mutableStateOf("") }
    var ruangan by remember { mutableStateOf("") }
    var selectedHari by remember { mutableStateOf("Senin") }
    var jamMulai by remember { mutableStateOf("") }
    var jamSelesai by remember { mutableStateOf("") }

    var expandedHari by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    var showJamMulaiPicker by remember { mutableStateOf(false) }
    var showJamSelesaiPicker by remember { mutableStateOf(false) }
    val jamMulaiState = rememberTimePickerState(is24Hour = true)
    val jamSelesaiState = rememberTimePickerState(is24Hour = true)
    var enableNotifikasi by remember { mutableStateOf(false) }


    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val hariList = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")

    // Time Picker Jam Mulai
    if (showJamMulaiPicker) {
        CustomTimePickerDialog(onDismissRequest = { showJamMulaiPicker = false }, onConfirm = {
            jamMulai = String.format(
                Locale.getDefault(), "%02d:%02d", jamMulaiState.hour, jamMulaiState.minute
            )
            showJamMulaiPicker = false
        }) {
            TimePicker(state = jamMulaiState)
        }
    }

    // Time Picker Jam Selesai
    if (showJamSelesaiPicker) {
        CustomTimePickerDialog(onDismissRequest = { showJamSelesaiPicker = false }, onConfirm = {
            jamSelesai = String.format(
                Locale.getDefault(), "%02d:%02d", jamSelesaiState.hour, jamSelesaiState.minute
            )
            showJamSelesaiPicker = false
        }) {
            TimePicker(state = jamSelesaiState)
        }
    }

    AlertDialog(onDismissRequest = onDismiss, title = {
        Text(
            "Tambah Jadwal Baru", style = MaterialTheme.typography.headlineSmall
        )
    }, text = {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Dropdown Hari
            ExposedDropdownMenuBox(
                expanded = expandedHari, onExpandedChange = { expandedHari = !expandedHari }) {
                OutlinedTextField(
                    value = selectedHari,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Hari") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedHari)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedHari, onDismissRequest = { expandedHari = false }) {
                    hariList.forEach { hari ->
                        DropdownMenuItem(text = { Text(hari) }, onClick = {
                            selectedHari = hari
                            expandedHari = false
                        })
                    }
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
                        .clickable { showJamMulaiPicker = true }) {
                    OutlinedTextField(
                        value = jamMulai.ifEmpty { "" },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Jam Mulai") },
                        placeholder = { Text("08:00") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = if (jamMulai.isBlank() && errorMessage != null) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.outline,
                            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
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
                        .clickable { showJamSelesaiPicker = true }) {
                    OutlinedTextField(
                        value = jamSelesai.ifEmpty { "" },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Jam Selesai") },
                        placeholder = { Text("10:00") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = if (jamSelesai.isBlank() && errorMessage != null) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.outline,
                            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        singleLine = true
                    )
                }
            }

            // Input Mata Kuliah
            OutlinedTextField(
                value = mataKuliah,
                onValueChange = { mataKuliah = it },
                label = { Text("Mata Kuliah") },
                placeholder = { Text("Contoh: Basis Data") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = mataKuliah.isBlank() && errorMessage != null
            )

            // Input Dosen
            OutlinedTextField(
                value = dosen,
                onValueChange = { dosen = it },
                label = { Text("Nama Dosen") },
                placeholder = { Text("Contoh: Dr. John Doe") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = dosen.isBlank() && errorMessage != null
            )

            // Input Ruangan
            OutlinedTextField(
                value = ruangan,
                onValueChange = { ruangan = it },
                label = { Text("Ruangan") },
                placeholder = { Text("Contoh: A101, Lab Komputer 1") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = ruangan.isBlank() && errorMessage != null
            )

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
        }
    }, confirmButton = {
        Button(
            onClick = {
                if (mataKuliah.isNotBlank() && dosen.isNotBlank() && ruangan.isNotBlank() && jamMulai.isNotBlank() && jamSelesai.isNotBlank()) {
                    val request = JadwalRequest(
                        mata_kuliah = mataKuliah.trim(),
                        dosen = dosen.trim(),
                        ruangan = ruangan.trim(),
                        hari = selectedHari,
                        jam_mulai = jamMulai,
                        jam_selesai = jamSelesai,
                        pasang_pengingat = enableNotifikasi
                    )
                    onConfirm(request)
                }
            },
            enabled = mataKuliah.isNotBlank() && dosen.isNotBlank() && ruangan.isNotBlank() && jamMulai.isNotBlank() && jamSelesai.isNotBlank()
        ) {
            Text("Simpan")
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text("Batal")
        }
    })
}