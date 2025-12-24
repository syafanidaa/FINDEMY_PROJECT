package com.example.findemy.ui.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tentang Kami",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "FinDemy adalah aplikasi inovatif yang dirancang khusus untuk membantu mahasiswa dalam mengelola aktivitas akademik sekaligus keuangan pribadi. Kami percaya bahwa keseimbangan antara manajemen waktu dan keuangan adalah kunci untuk mendukung produktivitas serta keberhasilan dalam menjalani masa perkuliahan.\n\nDengan FinDemy, mahasiswa dapat dengan mudah:\n\nMengatur jadwal kuliah dan kegiatan melalui kalender terintegrasi.\n\nMencatat dan memantau daftar tugas agar lebih terorganisir.\n\nMengelola pemasukan dan pengeluaran, sekaligus menyusun laporan keuangan sederhana yang jelas.\n\nVisi kami adalah menjadi teman digital yang mendampingi mahasiswa dalam membangun kebiasaan disiplin, produktif, dan bijak dalam mengatur keuangan.\n\nMisi kami adalah menghadirkan solusi praktis berbasis teknologi untuk mendukung kebutuhan akademik dan finansial mahasiswa dalam satu aplikasi terpadu.\n\nKami percaya, dengan FinDemy mahasiswa dapat fokus pada pengembangan diri dan prestasi akademik, tanpa khawatir kehilangan kendali atas waktu maupun keuangan.",
                fontSize = 14.sp,
                color = Color(0xFF333333),
                textAlign = TextAlign.Justify,
                lineHeight = 22.sp
            )
        }
    }
}
