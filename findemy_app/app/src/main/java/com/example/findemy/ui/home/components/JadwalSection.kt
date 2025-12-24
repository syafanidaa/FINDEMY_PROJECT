package com.example.findemy.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.findemy.R
import com.example.findemy.data.model.Jadwal

@Composable
fun JadwalSection(
    jadwals: List<Jadwal>, modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Header Section
        Text(
            text = "Jadwal Hari Ini",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (jadwals.isNotEmpty()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                jadwals.forEach { jadwal ->
                    JadwalCard(jadwal = jadwal)
                }
            }
        } else {
            EmptyJadwalCard()
        }
    }
}

@Composable
fun JadwalCard(
    jadwal: Jadwal, modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Transparent)
            .paint(painterResource(id = R.drawable.bg_card), contentScale = ContentScale.Crop)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = jadwal.mata_kuliah, style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold, color = Color.White
                ), lineHeight = 28.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoItem(
                    label = "Waktu",
                    value = "${jadwal.jam_mulai}-${jadwal.jam_selesai}"
                )
                InfoItem(
                    label = "Ruangan",
                    value = jadwal.ruangan
                )
                InfoItem(
                    label = "Dosen",
                    value = jadwal.dosen
                )
            }

        }
    }
}

@Composable
private fun InfoItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.8f),
            fontWeight = FontWeight.Normal
        )
        Text(
            text = value, fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun EmptyJadwalCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF7B9CFF), Color(0xFFB8B5FF)
                    )
                )
            )
            .padding(32.dp), contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Tidak ada jadwal untuk hari ini",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.White, fontWeight = FontWeight.Medium
            )
        )
    }
}