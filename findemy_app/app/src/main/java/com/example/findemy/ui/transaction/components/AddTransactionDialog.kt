package com.example.findemy.ui.transaction.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    rekeningList: List<Pair<Int, String>>, // List of (id, nama)
    onDismiss: () -> Unit,
    onConfirm: (rekeningId: Int, jenis: String, keterangan: String, jumlah: Int) -> Unit
) {
    var selectedRekeningId by remember { mutableStateOf<Int?>(null) }
    var jenis by remember { mutableStateOf("pemasukan") }
    var keterangan by remember { mutableStateOf("") }
    var jumlah by remember { mutableStateOf("") }

    var rekeningError by remember { mutableStateOf(false) }
    var keteranganError by remember { mutableStateOf(false) }
    var jumlahError by remember { mutableStateOf(false) }

    var expandedRekening by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Transaksi") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Dropdown Rekening
                ExposedDropdownMenuBox(
                    expanded = expandedRekening,
                    onExpandedChange = { expandedRekening = it }
                ) {
                    OutlinedTextField(
                        value = rekeningList.find { it.first == selectedRekeningId }?.second ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Pilih Rekening") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRekening) },
                        isError = rekeningError,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedRekening,
                        onDismissRequest = { expandedRekening = false }
                    ) {
                        rekeningList.forEach { (id, nama) ->
                            DropdownMenuItem(
                                text = { Text(nama) },
                                onClick = {
                                    selectedRekeningId = id
                                    expandedRekening = false
                                    rekeningError = false
                                }
                            )
                        }
                    }
                }

                if (rekeningError) {
                    Text(
                        text = "Pilih rekening terlebih dahulu",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                // Radio Button Jenis Transaksi
                Column(modifier = Modifier.selectableGroup()) {
                    Text(
                        text = "Jenis Transaksi",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .selectable(
                                    selected = jenis == "pemasukan",
                                    onClick = { jenis = "pemasukan" },
                                    role = Role.RadioButton
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = jenis == "pemasukan",
                                onClick = null,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color(0xFF079CD2)
                                )
                            )
                            Text(
                                text = "Pemasukan",
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .selectable(
                                    selected = jenis == "pengeluaran",
                                    onClick = { jenis = "pengeluaran" },
                                    role = Role.RadioButton
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = jenis == "pengeluaran",
                                onClick = null,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color(0xFF079CD2)
                                )
                            )
                            Text(
                                text = "Pengeluaran",
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }

                // Keterangan
                OutlinedTextField(
                    value = keterangan,
                    onValueChange = {
                        keterangan = it
                        keteranganError = false
                    },
                    label = { Text("Keterangan") },
                    isError = keteranganError,
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3
                )
                if (keteranganError) {
                    Text(
                        text = "Keterangan tidak boleh kosong",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                // Jumlah
                OutlinedTextField(
                    value = jumlah,
                    onValueChange = {
                        jumlah = it.filter { char -> char.isDigit() }
                        jumlahError = false
                    },
                    label = { Text("Jumlah") },
                    isError = jumlahError,
                    modifier = Modifier.fillMaxWidth(),
                    prefix = { Text("Rp ") }
                )
                if (jumlahError) {
                    Text(
                        text = "Jumlah harus berupa angka dan tidak boleh kosong",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    rekeningError = selectedRekeningId == null
                    keteranganError = keterangan.isBlank()
                    jumlahError = jumlah.isBlank()

                    if (!rekeningError && !keteranganError && !jumlahError) {
                        onConfirm(
                            selectedRekeningId!!,
                            jenis,
                            keterangan,
                            jumlah.toIntOrNull() ?: 0
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF079CD2)
                )
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