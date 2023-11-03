package com.koniukhov.waterreminder

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.koniukhov.waterreminder.data.drinkware.DrinkWare
import com.koniukhov.waterreminder.data.drinkware.DrinkWareDao
import com.koniukhov.waterreminder.database.AppDataBase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class DrinkWareInstrumentedTest {
    private lateinit var dataBase: AppDataBase
    private lateinit var drinkWareDao: DrinkWareDao
    private lateinit var drinkWare: List<DrinkWare>

    @Before
    fun createDb() = runTest{
        val context = ApplicationProvider.getApplicationContext<Context>()
        dataBase = Room.databaseBuilder(
            context,
            AppDataBase::class.java,
            "app_database")
            .createFromAsset("database/water_reminder.db")
            .build()
        drinkWareDao = dataBase.drinkWareDao()

        drinkWare = drinkWareDao.getDrinkWares().first()
    }

    @After
    fun closeDb() {
        dataBase.close()
    }

    @Test
    fun checkSizeEqual_9(){
        assertThat(drinkWare.size, equalTo(9) )
    }

    @Test
    fun checkContainsCup150Ml(){
        val cup150Ml = DrinkWare(4, 150, "ic_drink_ware_150_ml", 0)
        val cupInList = drinkWare.first { it.volume == 150 }

        assertThat(cupInList, equalTo(cup150Ml))
    }

    @Test
    fun checkCup200MlIsActive(){
        val cup200Ml = DrinkWare(6, 200, "ic_drink_ware_200_ml", 1)
        val cupInList = drinkWare.first { it.isActive == 1 }

        assertThat(cupInList, equalTo(cup200Ml))
    }


}