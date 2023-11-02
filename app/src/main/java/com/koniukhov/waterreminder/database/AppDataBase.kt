package com.koniukhov.waterreminder.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.koniukhov.waterreminder.data.drinkware.DrinkWare
import com.koniukhov.waterreminder.data.drinkware.DrinkWareDao

@Database(entities = [DrinkWare::class], version = 1)
abstract class AppDataBase : RoomDatabase() {
    abstract fun drinkWareDao(): DrinkWareDao

    companion object{
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDataBase(context: Context): AppDataBase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context,
                    AppDataBase::class.java,
                    "app_database")
                    .createFromAsset("database/water_reminder.db")
                    .build()

                INSTANCE = instance

                instance
            }
        }
    }

}