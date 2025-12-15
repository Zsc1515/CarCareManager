package com.elsalakan.carcaremanager.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cars")
data class Car(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val make: String,
    val model: String,
    val year: Int,
    val currentMileage: Int,
) {
    val displayName: String
        get() = "$year $make $model"
}