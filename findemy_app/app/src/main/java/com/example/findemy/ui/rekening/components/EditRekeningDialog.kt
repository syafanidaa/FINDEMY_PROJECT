package com.example.findemy.ui.rekening.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.findemy.data.model.Rekening


@Composable
fun EditRekeningDialog(
    rekening: Rekening,
    onDismiss: () -> Unit,
    onConfirm: (String, Int) -> Unit
) {
    var nama by remember { mutableStateOf(rekening.nama) }
    var saldo by remember { mutableStateOf(rekening.saldo.toString()) }
    var namaError by remember { mutableStateOf(false) }
    var saldoError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Rekening") },
        text = {
            Column {
                OutlinedTextField(
                    value = nama,
                    onValueChange = {
                        nama = it
                        namaError = false
                    },
                    label = { Text("Nama Rekening") },
                    isError = namaError,
                    modifier = Modifier.fillMaxWidth()
                )
                if (namaError) {
                    Text(
                        text = "Nama rekening tidak boleh kosong",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = saldo,
                    onValueChange = {
                        saldo = it.filter { char -> char.isDigit() }
                        saldoError = false
                    },
                    label = { Text("Saldo") },
                    isError = saldoError,
                    modifier = Modifier.fillMaxWidth()
                )
                if (saldoError) {
                    Text(
                        text = "Saldo harus berupa angka",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    namaError = nama.isBlank()
                    saldoError = saldo.isBlank()

                    if (!namaError && !saldoError) {
                        onConfirm(nama, saldo.toIntOrNull() ?: 0)
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
