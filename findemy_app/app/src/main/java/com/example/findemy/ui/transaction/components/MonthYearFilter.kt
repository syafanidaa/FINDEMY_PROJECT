package com.example.findemy.ui.transaction.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun MonthYearFilter(
    currentMonth: String,
    currentYear: String,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    val monthName = when (currentMonth) {
        "1", "01" -> "Januari"
        "2", "02" -> "Februari"
        "3", "03" -> "Maret"
        "4", "04" -> "April"
        "5", "05" -> "Mei"
        "6", "06" -> "Juni"
        "7", "07" -> "Juli"
        "8", "08" -> "Agustus"
        "9", "09" -> "September"
        "10" -> "Oktober"
        "11" -> "November"
        "12" -> "Desember"
        else -> currentMonth
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPrevious) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Bulan Sebelumnya",
                    tint = Color(0xFF079CD2)
                )
            }

            Text(
                text = "$monthName $currentYear",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = onNext) {
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "Bulan Berikutnya",
                    tint = Color(0xFF079CD2)
                )
            }
        }
    }
}