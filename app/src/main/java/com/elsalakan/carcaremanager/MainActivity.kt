package com.elsalakan.carcaremanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.elsalakan.carcaremanager.data.AppDatabase
import com.elsalakan.carcaremanager.ui.theme.CarScreen
import com.elsalakan.carcaremanager.ui.theme.MaintenanceScreen
import com.elsalakan.carcaremanager.ui.theme.StatisticsScreen
import com.elsalakan.carcaremanager.viewmodels.CarViewModel
import com.elsalakan.carcaremanager.viewmodels.MaintenanceViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val database = remember { AppDatabase.getDatabase(this) }

            val carViewModel = remember { CarViewModel(database.carDao()) }
            val maintenanceViewModel = remember {
                MaintenanceViewModel(
                    maintenanceDao = database.maintenanceDao(),
                    carDao = database.carDao()
                )
            }

            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "car"
            ) {
                composable("car") {
                    CarScreen(
                        onNavigateToMaintenance = {
                            navController.navigate("maintenance")
                            },
                        onNavigateToStatistics = {
                            navController.navigate("statistics")
                        },
                        viewModel = carViewModel
                    )
                }
                composable("maintenance") {
                    MaintenanceScreen(
                        onBack = { navController.popBackStack() },
                        viewModel = maintenanceViewModel
                    )
                }
                composable("statistics") {
                    StatisticsScreen(
                        onBack = { navController.popBackStack() },
                        carViewModel = carViewModel,
                        maintenanceViewModel = maintenanceViewModel
                    )
                }
            }
        }
    }
}
