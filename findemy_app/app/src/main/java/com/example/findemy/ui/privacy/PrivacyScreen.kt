package com.example.findemy.ui.privacy

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
fun PrivacyScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Kebijakan Privasi",
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
                text = "FinDemy berkomitmen untuk melindungi privasi dan data pribadi setiap pengguna. Data yang dikumpulkan meliputi informasi akun (nama, email, kata sandi), data akademik (jadwal kuliah, daftar tugas, kalender kegiatan), serta data keuangan (pemasukan, pengeluaran, laporan).\n\nData tersebut digunakan untuk menyediakan layanan utama FinDemy, meningkatkan pengalaman pengguna, serta mendukung pengembangan fitur aplikasi. FinDemy tidak akan membagikan data pribadi kepada pihak ketiga tanpa izin, kecuali diwajibkan oleh hukum.\n\nKami menerapkan langkah keamanan untuk melindungi data pengguna, namun pengguna juga bertanggung jawab menjaga kerahasiaan akun mereka. Pengguna memiliki hak untuk memperbarui, menghapus, atau meminta informasi terkait data pribadi kapan saja.\n\nFinDemy dapat memperbarui Kebijakan Privasi sesuai kebutuhan dan perubahan hukum, dengan pemberitahuan kepada pengguna apabila terdapat perubahan signifikan.",
                fontSize = 14.sp,
                color = Color(0xFF333333),
                textAlign = TextAlign.Justify,
                lineHeight = 22.sp

            )
        }
    }
}
