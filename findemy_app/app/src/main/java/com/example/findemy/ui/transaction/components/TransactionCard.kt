package com.example.findemy.ui.transaction.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.findemy.data.model.Transaksi
import com.example.findemy.ui.transaction.formatCurrency
import com.example.findemy.ui.transaction.formatDate


@Composable
fun TransactionCard(transaksi: Transaksi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaksi.keterangan,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = transaksi.rekening.nama,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = formatDate(transaksi.created_at),
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Text(
                text = "${if (transaksi.jenis == "pemasukan") "+" else "-"} ${
                    formatCurrency(
                        transaksi.jumlah.toString()
                    )
                }",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (transaksi.jenis == "pemasukan") Color(0xFF4CAF50) else Color(0xFFF44336)
            )
        }
    }
}
