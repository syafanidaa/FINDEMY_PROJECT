package com.example.findemy.ui.task.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.findemy.data.model.Tugas
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineSection(
    title: String,
    tugasList: List<Tugas>,
    onEditTugas: (Tugas) -> Unit,
    onDeleteTugas: (Tugas) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var sortBy by remember { mutableStateOf("date") }
    var expanded by remember { mutableStateOf(false) }

    // Filter tugas berdasarkan pencarian
    val filteredTugas = tugasList.filter {
        it.judul.contains(searchQuery, ignoreCase = true) || it.jadwal.mata_kuliah.contains(
            searchQuery, ignoreCase = true
        )
    }

    // Group tugas berdasarkan pilihan sort
    val groupedTugas = when (sortBy) {
        "course" -> filteredTugas.sortedBy { it.jadwal.mata_kuliah }
            .groupBy { it.jadwal.mata_kuliah }

        else -> filteredTugas.sortedBy { it.deadline }.groupBy { formatTanggal(it.deadline) }
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFDCDCDC), RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header Timeline
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        )

        // Search Bar dan Sort Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Search TextField
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = "Cari berdasarkan",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF8A93D7),
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                ),
                singleLine = true
            )

            // Sort Button
            Box {
                IconButton(
                    onClick = { expanded = true },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF5F5F5))
                ) {
                    Icon(
                        Icons.Default.Sort,
                        contentDescription = "Sort",
                        tint = Color(0xFF8A93D7)
                    )
                }

                // Dropdown Menu
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Sort by date") },
                        onClick = {
                            sortBy = "date"
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Sort by course") },
                        onClick = {
                            sortBy = "course"
                            expanded = false
                        }
                    )
                }
            }
        }

        // List Tugas
        if (groupedTugas.isEmpty()) {
            Text(
                text = "Tidak ada tugas",
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                groupedTugas.forEach { (groupKey, tugasPerGroup) ->
                    // Group Header
                    Text(
                        text = groupKey,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black.copy(alpha = 0.6f)
                        ),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 12.dp)
                    )

                    // Tugas Cards dalam group
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        tugasPerGroup.forEach { tugas ->
                            TugasCard(
                                tugas = tugas,
                                groupBy = sortBy,
                                onEdit = onEditTugas,
                                onDelete = onDeleteTugas
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TugasCard(
    tugas: Tugas,
    groupBy: String,
    onEdit: (Tugas) -> Unit,
    onDelete: (Tugas) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    val formattedTime = try {
        tugas.deadline.substring(11, 16) // Ambil jam:menit
    } catch (e: Exception) {
        "00:00"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF8A93D7))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time
            Text(
                text = formattedTime,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier.width(50.dp)
            )

            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = if (groupBy == "course") formatTanggal(tugas.deadline)
                    else tugas.jadwal.mata_kuliah,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = tugas.judul,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.9f)
                    ),
                    fontSize = 13.sp
                )
            }

            // Menu Button (3 dots)
            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menu",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = {
                            showMenu = false
                            onEdit(tugas)
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Hapus") },
                        onClick = {
                            showMenu = false
                            showDeleteDialog = true
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    )
                }
            }
        }
    }

    // Dialog Konfirmasi Hapus
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    "Hapus Tugas?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Apakah Anda yakin ingin menghapus tugas ini?",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDelete(tugas)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Hapus", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal", fontWeight = FontWeight.Medium)
                }
            }
        )
    }
}

// Helper function untuk format tanggal
private fun formatTanggal(deadline: String): String {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val date = LocalDateTime.parse(deadline, formatter)
        val formatterOutput = DateTimeFormatter.ofPattern(
            "EEEE, d MMMM yyyy", Locale("id", "ID")
        )
        date.format(formatterOutput)
    } catch (e: Exception) {
        "-"
    }
}