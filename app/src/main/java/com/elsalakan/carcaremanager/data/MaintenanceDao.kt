package com.elsalakan.carcaremanager.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceDao {
    @Insert
    suspend fun insertMaintenance(maintenance: Maintenance)

    @Update
    suspend fun updateMaintenance(maintenance: Maintenance)

    @Delete
    suspend fun deleteMaintenance(maintenance: Maintenance)

    @Query("SELECT * FROM maintenance ORDER BY date DESC")
    fun getAllMaintenance(): Flow<List<Maintenance>>
}