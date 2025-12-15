package com.elsalakan.carcaremanager.ui.theme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.elsalakan.carcaremanager.data.Car
import com.elsalakan.carcaremanager.data.Maintenance
import com.elsalakan.carcaremanager.viewmodels.CarViewModel
import com.elsalakan.carcaremanager.viewmodels.MaintenanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onBack: () -> Unit,
    carViewModel: CarViewModel,
    maintenanceViewModel: MaintenanceViewModel
) {
    val cars by carViewModel.cars.collectAsState()
    val maintenanceList by maintenanceViewModel.maintenanceList.collectAsState()

    // =========== STATISTICS ============
    val totalCars = cars.size
    val totalMaintenance = maintenanceList.size
    val totalCost = maintenanceList.sumOf { it.cost }
    val totalMileage = cars.sumOf { it.currentMileage }
    val carsWithMaintenance = maintenanceList.map { it.carId }.distinct().count()

    val mostExpensiveService = maintenanceList.maxByOrNull { it.cost }
    val averageCost = if (totalMaintenance > 0) totalCost / totalMaintenance else 0.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📊 Statistics") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            StatCard("Total Cars", "$totalCars")
            StatCard("Total Maintenance Services", "$totalMaintenance")
            StatCard("Total Mileage", "$totalMileage km")
            StatCard("Total Cost", "EGP ${"%.2f".format(totalCost)}")
            StatCard("Average Cost per Service", "EGP ${"%.2f".format(averageCost)}")
            StatCard("Cars with Services", "$carsWithMaintenance")

            if (mostExpensiveService != null) {
                StatCard(
                    title = "Most Expensive Service",
                    value = "${mostExpensiveService.serviceType}: EGP ${"%.2f".format(mostExpensiveService.cost)}"
                )
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}
