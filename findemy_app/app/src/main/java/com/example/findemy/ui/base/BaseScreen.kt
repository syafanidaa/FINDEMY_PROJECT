package com.example.findemy.ui.base

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.findemy.R
import com.example.findemy.ui.home.HomePage
import com.example.findemy.ui.schedule.SchedulePage
import com.example.findemy.ui.calendar.CalendarPage
import com.example.findemy.ui.task.TaskPage
import com.example.findemy.ui.transaction.TransactionPage

@Composable
fun BaseScreen(rootNavController: NavController) {
    val context = LocalContext.current

    // === Izin notifikasi untuk Android 13+ ===
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { granted ->
                if (granted) {
                    Toast.makeText(context, "Notifikasi diizinkan", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Izin notifikasi ditolak", Toast.LENGTH_SHORT).show()
                }
            }
        )

        LaunchedEffect(Unit) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    } else {
        LaunchedEffect(Unit) {
        }
    }

    val navController = rememberNavController()

    val items = listOf(
        BottomItem(
            label = "Schedule",
            iconInactive = R.drawable.ic_nav_1_inactive,
            iconActive = R.drawable.ic_nav_1_active,
            route = "schedule"
        ),
        BottomItem(
            label = "Calendar",
            iconInactive = R.drawable.ic_nav_2_inactive,
            iconActive = R.drawable.ic_nav_2_active,
            route = "calendar"
        ),
        BottomItem(
            label = "Home",
            iconInactive = R.drawable.ic_nav_3_inactive,
            iconActive = R.drawable.ic_nav_3_active,
            route = "home"
        ),
        BottomItem(
            label = "Task",
            iconInactive = R.drawable.ic_nav_4_inactive,
            iconActive = R.drawable.ic_nav_4_active,
            route = "task"
        ),
        BottomItem(
            label = "Transaction",
            iconInactive = R.drawable.ic_nav_5_inactive,
            iconActive = R.drawable.ic_nav_5_active,
            route = "transaction"
        )
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.height(110.dp)
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { item ->
                    val selected = currentRoute == item.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(
                                    id = if (selected) item.iconActive else item.iconInactive
                                ),
                                contentDescription = item.label,
                                tint = Color.Unspecified,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        alwaysShowLabel = false,
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent,
                            selectedIconColor = Color.Unspecified,
                            unselectedIconColor = Color.Unspecified
                        )
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            composable("home") { HomePage(rootNavController) }
            composable("schedule") { SchedulePage(rootNavController) }
            composable("calendar") { CalendarPage(rootNavController) }
            composable("task") { TaskPage(rootNavController) }
            composable("transaction") { TransactionPage(rootNavController) }
        }
    }
}

data class BottomItem(
    val label: String,
    @DrawableRes val iconInactive: Int,
    @DrawableRes val iconActive: Int,
    val route: String
)