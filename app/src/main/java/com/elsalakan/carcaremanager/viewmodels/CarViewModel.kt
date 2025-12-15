package com.elsalakan.carcaremanager.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elsalakan.carcaremanager.data.Car
import com.elsalakan.carcaremanager.data.CarDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CarViewModel(private val carDao: CarDao) : ViewModel() {
    private val _cars = MutableStateFlow<List<Car>>(emptyList())
    val cars: StateFlow<List<Car>> = _cars.asStateFlow()

    init {
        loadCars()
        addDefaultCar()
    }

    fun loadCars() {
        viewModelScope.launch {
            carDao.getAllCars().collect { cars ->
                _cars.value = cars
            }
        }
    }

    private fun addDefaultCar() {
        viewModelScope.launch {
            if (_cars.value.isEmpty()) {
                val defaultCar = Car(
                    make = "Toyota",
                    model = "Camry",
                    year = 2022,
                    currentMileage = 15000
                )
                addCar(defaultCar)
            }
        }
    }

    fun addCar(car: Car) {
        viewModelScope.launch {
            carDao.insertCar(car)
        }
    }

    fun updateCar(car: Car) {
        viewModelScope.launch {
            carDao.updateCar(car)
        }
    }

    fun deleteCar(car: Car) {
        viewModelScope.launch {
            carDao.deleteCar(car)
        }
    }

    fun filterCars(filter: (Car) -> Boolean): List<Car> {
        return _cars.value.filter(filter)
    }

    fun getAllCarNames(): List<String> {
        return _cars.value.map { "${it.make} ${it.model}" }
    }

    fun getTotalMileage(): Int {
        return _cars.value.sumOf { it.currentMileage }
    }

    fun findCarByName(name: String): Car? {
        return _cars.value.find { it.displayName.contains(name, ignoreCase = true) }
    }
}