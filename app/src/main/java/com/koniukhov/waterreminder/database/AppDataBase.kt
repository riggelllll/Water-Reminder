package com.koniukhov.waterreminder.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.koniukhov.waterreminder.data.dailywater.DailyWater
import com.koniukhov.waterreminder.data.dailywater.DailyWaterDao
import com.koniukhov.waterreminder.data.drinkware.DrinkWare
import com.koniukhov.waterreminder.data.drinkware.DrinkWareDao

const val DATABASE_NAME = "app_database"
const val ASSET_DATABASE = "database/water_reminder.db"

@Database(entities = [DailyWater::class, DrinkWare::class], version = 1)
abstract class AppDataBase : RoomDatabase() {
    abstract fun drinkWareDao(): DrinkWareDao
    abstract fun dailyWaterDao(): DailyWaterDao

    companion object{
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDataBase(context: Context): AppDataBase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context,
                    AppDataBase::class.java,
                    DATABASE_NAME)
                    .createFromAsset(ASSET_DATABASE)
                    .build()

                INSTANCE = instance

                instance
            }
        }
    }

}