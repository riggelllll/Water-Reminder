package com.koniukhov.waterreminder.data.dailywater

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyWaterDao {

    @Query("SELECT * FROM DailyWater WHERE date = :date ")
    fun getAllByDate(date: String): Flow<List<DailyWater>>

    @Query("SELECT * FROM DailyWater WHERE date LIKE :yearMonth || '%'")
    fun getAllByMonth(yearMonth: String): Flow<List<DailyWater>>

    @Insert
    suspend fun addDailyWater(dailyWater: DailyWater)

    @Delete
    suspend fun delete(dailyWater: DailyWater)
}