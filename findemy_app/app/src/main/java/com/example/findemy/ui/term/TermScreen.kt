package com.example.findemy.ui.term

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
fun TermScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ketentuan Layanan",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }, navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack, contentDescription = "Back"
                        )
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "FinDemy merupakan aplikasi yang ditujukan untuk membantu mahasiswa dalam mengatur jadwal kuliah, daftar tugas, serta keuangan pribadi. Dengan menggunakan aplikasi ini, pengguna dianggap setuju untuk memanfaatkan layanan hanya untuk kepentingan pribadi dan sesuai dengan fungsinya.\n\nPengguna bertanggung jawab penuh atas keamanan akun, kerahasiaan kata sandi, serta kebenaran data yang dimasukkan ke dalam aplikasi. FinDemy tidak bertanggung jawab atas kerugian yang timbul akibat kesalahan input, penyalahgunaan akun, atau kendala teknis di luar kendali kami.\n\nKami berhak melakukan perubahan fitur, pembaruan layanan, maupun penyesuaian kebijakan sewaktu-waktu untuk meningkatkan kualitas aplikasi. FinDemy juga berhak membatasi atau menghentikan akses bagi pengguna yang terbukti melanggar ketentuan ini.",
                fontSize = 14.sp,
                color = Color(0xFF333333),
                textAlign = TextAlign.Justify,
                lineHeight = 22.sp
            )
        }
    }
}
