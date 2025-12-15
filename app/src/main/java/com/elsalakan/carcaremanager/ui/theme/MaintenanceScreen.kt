package com.elsalakan.carcaremanager.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CarRepair
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.elsalakan.carcaremanager.data.Car
import com.elsalakan.carcaremanager.data.Maintenance
import com.elsalakan.carcaremanager.viewmodels.MaintenanceViewModel
import android.content.Intent
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceScreen(
    onBack: () -> Unit,
    viewModel: MaintenanceViewModel
) {
    val maintenanceList by viewModel.maintenanceList.collectAsState()
    val cars by viewModel.cars.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Maintenance Log") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (cars.isNotEmpty()) {
                        showAddDialog = true
                    }
                },
                containerColor = if (cars.isNotEmpty()) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Maintenance")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            val totalCost = remember(maintenanceList) { viewModel.getTotalCost() }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "Total Services: ${maintenanceList.size}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Total Cost: $${"%.2f".format(totalCost)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Cars with services: ${maintenanceList.map { it.carId }.distinct().count()}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (cars.isEmpty()) {
                NoCarsState()
            } else if (maintenanceList.isEmpty()) {
                EmptyMaintenanceState()
            } else {
                MaintenanceList(
                    maintenanceList = maintenanceList,
                    cars = cars,
                    viewModel = viewModel
                )
            }
        }
    }

    if (showAddDialog && cars.isNotEmpty()) {
        AddMaintenanceDialog(
            cars = cars,
            onDismiss = { showAddDialog = false },
            onAddMaintenance = { maintenance ->
                viewModel.addMaintenance(maintenance)
                showAddDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMaintenanceDialog(
    cars: List<Car>,
    onDismiss: () -> Unit,
    onAddMaintenance: (Maintenance) -> Unit
) {
    var selectedCarId by remember { mutableStateOf(cars.firstOrNull()?.id ?: 0) }
    var serviceType by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var mileage by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Maintenance Service") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (cars.isNotEmpty()) {
                    var expanded by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = cars.find { it.id == selectedCarId }?.displayName ?: "Select Car",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select Car *") },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                        )
                        var expanded by remember { mutableStateOf(false) }

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = cars.find { it.id == selectedCarId }?.displayName ?: "Select Car",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Select Car") },
                                modifier = Modifier
                                    .menuAnchor()   // مهم جدًا جدًا علشان القايمة تظهر صح
                                    .fillMaxWidth(),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = expanded
                                    )
                                }
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                cars.forEach { car ->
                                    DropdownMenuItem(
                                        text = { Text(car.displayName) },
                                        onClick = {
                                            selectedCarId = car.id   // <-- هنا التغيير الأساسي
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                    }
                }

                OutlinedTextField(
                    value = serviceType,
                    onValueChange = { serviceType = it },
                    label = { Text("Service Type *") },
                    placeholder = { Text("e.g., Oil Change") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (DD/MM/YYYY) *") },
                    placeholder = { Text("e.g., 15/11/2023") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = mileage,
                    onValueChange = { mileage = it },
                    label = { Text("Mileage (km) *") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = cost,
                    onValueChange = { cost = it },
                    label = { Text("Cost (EGP) *") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val maintenance = Maintenance(
                        carId = selectedCarId,
                        serviceType = serviceType,
                        date = date,
                        mileage = mileage.toIntOrNull() ?: 0,
                        cost = cost.toDoubleOrNull() ?: 0.0,
                        notes = notes
                    )
                    onAddMaintenance(maintenance)
                },
                enabled = selectedCarId > 0 && serviceType.isNotEmpty() &&
                        date.isNotEmpty() && mileage.isNotEmpty() && cost.isNotEmpty()
            ) {
                Text("Add Service")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMaintenanceDialog(
    maintenance: Maintenance,
    cars: List<Car>,
    onDismiss: () -> Unit,
    onUpdate: (Maintenance) -> Unit
) {
    var selectedCarId by remember { mutableStateOf(maintenance.carId) }
    var serviceType by remember { mutableStateOf(maintenance.serviceType) }
    var date by remember { mutableStateOf(maintenance.date) }
    var mileage by remember { mutableStateOf(maintenance.mileage.toString()) }
    var cost by remember { mutableStateOf(maintenance.cost.toString()) }
    var notes by remember { mutableStateOf(maintenance.notes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Maintenance Service") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // Select Car
                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = cars.find { it.id == selectedCarId }?.displayName ?: "Select Car",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Car *") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                        }
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        cars.forEach { car ->
                            DropdownMenuItem(
                                text = { Text(car.displayName) },
                                onClick = {
                                    selectedCarId = car.id
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = serviceType,
                    onValueChange = { serviceType = it },
                    label = { Text("Service Type *") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (DD/MM/YYYY) *") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = mileage,
                    onValueChange = { mileage = it },
                    label = { Text("Mileage *") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = cost,
                    onValueChange = { cost = it },
                    label = { Text("Cost *") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updated = maintenance.copy(
                        carId = selectedCarId,
                        serviceType = serviceType,
                        date = date,
                        mileage = mileage.toIntOrNull() ?: 0,
                        cost = cost.toDoubleOrNull() ?: 0.0,
                        notes = notes
                    )
                    onUpdate(updated)
                },
                enabled = serviceType.isNotEmpty() &&
                        date.isNotEmpty() &&
                        mileage.isNotEmpty() &&
                        cost.isNotEmpty()
            ) {
                Text("Update")
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
fun MaintenanceList(
    maintenanceList: List<Maintenance>,
    cars: List<Car>,
    viewModel: MaintenanceViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(maintenanceList) { maintenance ->
            val car = cars.find { it.id == maintenance.carId }
            MaintenanceItem(
                maintenance = maintenance,
                car = car,
                cars = cars,
                viewModel = viewModel
            )
        }
    }
}

@Composable
//intent her
fun MaintenanceItem(
    maintenance: Maintenance,
    car: Car?,
    cars: List<Car>,
    viewModel: MaintenanceViewModel
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            if (car != null) {
                Text(
                    text = "🚗 ${car.make} ${car.model}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Text(
                text = maintenance.serviceType,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "📅 Date: ${maintenance.date}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "💰 Cost: ${"%.2f".format(maintenance.cost)} EGP",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "📏 Mileage: ${maintenance.mileage} km",
                style = MaterialTheme.typography.bodyMedium
            )

            if (maintenance.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "📝 Notes: ${maintenance.notes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Buttons: Edit, Delete, Share
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Edit button (opens edit dialog)
                Button(
                    onClick = { showEditDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Edit Service")
                }

                // Delete button
                Button(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Service")
                }

                // Share button -> Intent.ACTION_SEND
                Button(
                    onClick = {
                        val message = buildString {
                            append("Car: ${car?.displayName ?: "Unknown"}\n")
                            append("Service: ${maintenance.serviceType}\n")
                            append("Date: ${maintenance.date}\n")
                            append("Mileage: ${maintenance.mileage} km\n")
                            append("Cost: EGP ${"%.2f".format(maintenance.cost)}\n")
                            if (maintenance.notes.isNotBlank()) append("Notes: ${maintenance.notes}\n")
                            append("\nShared from CarCare Manager")
                        }

                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Maintenance Report: ${car?.displayName ?: ""}")
                            putExtra(Intent.EXTRA_TEXT, message)
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Share Maintenance")
                        context.startActivity(shareIntent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Share Service")
                }
            }
        }
    }

    // Delete dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Service") },
            text = {
                Column {
                    Text("Are you sure you want to delete this service?")
                    if (car != null) {
                        Text(
                            text = "${maintenance.serviceType} for ${car.make} ${car.model}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteMaintenance(maintenance)
                        showDeleteDialog = false
                    }
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Edit dialog (if you added EditMaintenanceDialog earlier)
    if (showEditDialog) {
        EditMaintenanceDialog(
            maintenance = maintenance,
            cars = cars,
            onDismiss = { showEditDialog = false },
            onUpdate = { updated ->
                viewModel.updateMaintenance(updated)
                showEditDialog = false
            }
        )
    }
}


@Composable
fun EmptyMaintenanceState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CarRepair,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Maintenance Records",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "Tap the + button to add your first service",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun NoCarsState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CarRepair,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Cars Available",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.error
        )
        Text(
            text = "Add cars first before adding maintenance",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Go back and add at least one car",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}
