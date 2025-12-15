package com.elsalakan.carcaremanager.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.elsalakan.carcaremanager.data.Car
import com.elsalakan.carcaremanager.viewmodels.CarViewModel
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Call
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarScreen(
    onNavigateToMaintenance: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    viewModel: CarViewModel
) {
    val cars by viewModel.cars.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Car")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🚗 CarCare Manager",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // إحصائية
            val totalMileage = remember(cars) { viewModel.getTotalMileage() }
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Total Cars: ${cars.size} | Total Mileage: $totalMileage km",
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (cars.isEmpty()) {
                EmptyCarState()
            } else {
                CarList(cars = cars, viewModel = viewModel)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // أزرار التنقل
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onNavigateToMaintenance,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("📋 Maintenance Log")
                }

                Button(
                    onClick = { onNavigateToStatistics() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("📊 Statistics")
                }

            }
        }
    }

    // دايالوج إضافة سيارة
    if (showAddDialog) {
        AddCarDialog(
            onDismiss = { showAddDialog = false },
            onAddCar = { car ->
                viewModel.addCar(car)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AddCarDialog(
    onDismiss: () -> Unit,
    onAddCar: (Car) -> Unit
) {
    var make by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var mileage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Car") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = make,
                    onValueChange = { make = it },
                    label = { Text("Make (e.g., Toyota)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it },
                    label = { Text("Model (e.g., Camry)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = year,
                    onValueChange = { year = it },
                    label = { Text("Year") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = mileage,
                    onValueChange = { mileage = it },
                    label = { Text("Mileage (km)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newCar = Car(
                        make = make,
                        model = model,
                        year = year.toIntOrNull() ?: 2023,
                        currentMileage = mileage.toIntOrNull() ?: 0
                    )
                    onAddCar(newCar)
                },
                enabled = make.isNotEmpty() && model.isNotEmpty() && year.isNotEmpty() && mileage.isNotEmpty()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CarList(cars: List<Car>, viewModel: CarViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(cars) { car ->
            CarItem(car = car, viewModel = viewModel)
        }
    }
}

@Composable
fun CarItem(car: Car, viewModel: CarViewModel) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = car.displayName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Mileage: ${car.currentMileage} km",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Delete button
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    // Delete dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Car") },
            text = { Text("Are you sure you want to delete this car?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCar(car)
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun EmptyCarState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No Cars Added",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "Tap the + button to add your first car",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}