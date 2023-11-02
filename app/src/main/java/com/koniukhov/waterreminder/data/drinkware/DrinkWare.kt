package com.koniukhov.waterreminder.data.drinkware

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DrinkWare(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "volume")
    val volume: Int,
    @ColumnInfo(name = "icon_name")
    val iconName: String,
    @ColumnInfo(name = "is_active")
    val isActive: Int
)
