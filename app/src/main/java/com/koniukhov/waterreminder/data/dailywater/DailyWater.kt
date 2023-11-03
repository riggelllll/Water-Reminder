package com.koniukhov.waterreminder.data.dailywater

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DailyWater(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "volume")
    val volume: Int,
    @ColumnInfo(name = "time")
    val time: Int,
    @ColumnInfo(name = "date")
    val date: String,
    @ColumnInfo(name = "icon_name")
    val iconName: String
)



