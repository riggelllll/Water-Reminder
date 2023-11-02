package com.koniukhov.waterreminder.data.drinkware

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DrinkWareDao {

    @Query("SELECT * FROM DrinkWare")
    fun getDrinkWares(): Flow<List<DrinkWare>>

    @Update
    suspend fun update(drinkWare: DrinkWare)
}