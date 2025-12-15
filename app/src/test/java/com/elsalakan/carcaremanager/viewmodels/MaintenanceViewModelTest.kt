package com.elsalakan.carcaremanager.viewmodels

import com.elsalakan.carcaremanager.data.CarDao
import com.elsalakan.carcaremanager.data.Maintenance
import com.elsalakan.carcaremanager.data.MaintenanceDao
import com.elsalakan.carcaremanager.utiltest.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class MaintenanceViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var maintenanceDao: MaintenanceDao
    private lateinit var carDao: CarDao
    private lateinit var viewModel: MaintenanceViewModel

    @Before
    fun setup() {
        maintenanceDao = mock()
        carDao = mock()

        whenever(maintenanceDao.getAllMaintenance()).thenReturn(flowOf(emptyList()))
        whenever(carDao.getAllCars()).thenReturn(flowOf(emptyList()))

        viewModel = MaintenanceViewModel(maintenanceDao, carDao)
    }

    @Test
    fun addMaintenance_callsInsertMaintenance() = runTest {
        val maintenance = Maintenance(
            carId = 1,
            serviceType = "Oil Change",
            date = "10/10/2024",
            mileage = 20000,
            cost = 500.0
        )

        viewModel.addMaintenance(maintenance)

        verify(maintenanceDao).insertMaintenance(maintenance)
    }

    @Test
    fun deleteMaintenance_callsDeleteMaintenance() = runTest {
        val maintenance = Maintenance(
            id = 1,
            carId = 1,
            serviceType = "Brake",
            date = "11/11/2024",
            mileage = 25000,
            cost = 1200.0
        )

        viewModel.deleteMaintenance(maintenance)

        verify(maintenanceDao).deleteMaintenance(maintenance)
    }

    @Test
    fun getTotalCost_returnsCorrectSum() = runTest {
        val list = listOf(
            Maintenance(1, 1, "Oil", "1/1/2024", 10000, 300.0),
            Maintenance(2, 1, "Brake", "2/2/2024", 12000, 700.0)
        )

        whenever(maintenanceDao.getAllMaintenance()).thenReturn(flowOf(list))

        viewModel.loadMaintenance()

        val total = viewModel.getTotalCost()

        assertEquals(1000.0, total, 0.0)
    }
}
