package com.elsalakan.carcaremanager.viewmodels

import com.elsalakan.carcaremanager.data.Car
import com.elsalakan.carcaremanager.data.CarDao
import com.elsalakan.carcaremanager.utiltest.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class CarViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var carDao: CarDao
    private lateinit var viewModel: CarViewModel

    @Before
    fun setup() {
        carDao = mock()
        whenever(carDao.getAllCars()).thenReturn(flowOf(emptyList()))
        viewModel = CarViewModel(carDao)
    }

    @Test
    fun addCar_callsInsertCar() = runTest {
        val car = Car(
            make = "BMW",
            model = "X5",
            year = 2021,
            currentMileage = 20000
        )

        viewModel.addCar(car)

        verify(carDao).insertCar(car)
    }

    @Test
    fun deleteCar_callsDeleteCar() = runTest {
        val car = Car(
            id = 1,
            make = "Toyota",
            model = "Corolla",
            year = 2020,
            currentMileage = 15000
        )

        viewModel.deleteCar(car)

        verify(carDao).deleteCar(car)
    }

    @Test
    fun filterCars_returnsCorrectResult() {
        val cars = listOf(
            Car(1, "Toyota", "Camry", 2022, 15000),
            Car(2, "BMW", "X5", 2021, 30000)
        )

        whenever(carDao.getAllCars()).thenReturn(flowOf(cars))
        viewModel = CarViewModel(carDao)

        val result = viewModel.filterCars { it.make == "BMW" }

        assertEquals(1, result.size)
        assertEquals("BMW", result.first().make)
    }
}
