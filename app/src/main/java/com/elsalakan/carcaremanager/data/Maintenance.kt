package com.elsalakan.carcaremanager.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "maintenance",
    foreignKeys = [
        ForeignKey(
            entity = Car::class,
            parentColumns = ["id"],
            childColumns = ["carId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["carId"])]
)
data class Maintenance(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val carId: Int,  // <-- دي الحقل الجديد
    val serviceType: String,
    val date: String,
    val mileage: Int,
    val cost: Double,
    val notes: String = ""
) {
    // Lambda كـ property
    val formattedCost: String get() = "$${"%.2f".format(cost)}"

    // Helper function
    fun isExpensive(): Boolean = cost > 1000
}