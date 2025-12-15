package com.elsalakan.carcaremanager.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elsalakan.carcaremanager.data.Car
import com.elsalakan.carcaremanager.data.CarDao
import com.elsalakan.carcaremanager.data.Maintenance
import com.elsalakan.carcaremanager.data.MaintenanceDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MaintenanceViewModel(
    private val maintenanceDao: MaintenanceDao,
    private val carDao: CarDao
) : ViewModel() {
    private val _maintenanceList = MutableStateFlow<List<Maintenance>>(emptyList())
    val maintenanceList: StateFlow<List<Maintenance>> = _maintenanceList.asStateFlow()

    private val _cars = MutableStateFlow<List<Car>>(emptyList())
    val cars: StateFlow<List<Car>> = _cars.asStateFlow()

    init {
        loadMaintenance()
        loadCars()
    }

    fun loadMaintenance() {
        viewModelScope.launch {
            maintenanceDao.getAllMaintenance().collect { list ->
                _maintenanceList.value = list
            }
        }
    }

    private fun loadCars() {
        viewModelScope.launch {
            carDao.getAllCars().collect { cars ->
                _cars.value = cars
            }
        }
    }

    fun addMaintenance(maintenance: Maintenance) {
        viewModelScope.launch {
            maintenanceDao.insertMaintenance(maintenance)
        }
    }

    fun deleteMaintenance(maintenance: Maintenance) {
        viewModelScope.launch {
            maintenanceDao.deleteMaintenance(maintenance)
        }
    }

    fun updateMaintenance(maintenance: Maintenance) {
        viewModelScope.launch {
            maintenanceDao.updateMaintenance(maintenance)
        }
    }

    fun getTotalCost(): Double {
        return _maintenanceList.value.sumOf { it.cost }
    }
}