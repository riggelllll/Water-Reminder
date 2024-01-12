package com.koniukhov.waterreminder

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.koniukhov.waterreminder.data.dailywater.DailyWater
import com.koniukhov.waterreminder.data.dailywater.DailyWaterDao
import com.koniukhov.waterreminder.database.AppDataBase
import com.koniukhov.waterreminder.utilities.yearMonthFormat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalTime

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class DailyWaterInstrumentedTest {
    private lateinit var dataBase: AppDataBase
    private lateinit var dailyWaterDao: DailyWaterDao
    private lateinit var dailyWater: List<DailyWater>

    @Before
    fun createDb() = runTest{
        val context = ApplicationProvider.getApplicationContext<Context>()
        dataBase = Room.databaseBuilder(
            context,
            AppDataBase::class.java,
            "app_database")
            .createFromAsset("database/water_reminder.db")
            .build()
        dailyWaterDao = dataBase.dailyWaterDao()
    }

    @After
    fun closeDb() {
        dataBase.close()
    }

    @Test
    fun checkItemsSizeAfterAddingEquals_1() = runTest{
        val item = DailyWater(null, 200, LocalTime.now().toString(), LocalDate.now().toString(), "icon")
        dailyWaterDao.addDailyWater(item)

        dailyWater = dailyWaterDao.getAllByDate(LocalDate.now().toString()).first()

        assertThat(dailyWater.size, equalTo(1))
    }

    @Test
    fun checkItemsSizeAfterAddingEquals_4() = runTest{
        val item1 = DailyWater(null, 200, LocalTime.now().toString(), LocalDate.now().toString(), "icon")
        val item2 = DailyWater(null, 300, LocalTime.now().toString(), LocalDate.now().toString(), "icon")
        val item3 = DailyWater(null, 150, LocalTime.now().toString(), LocalDate.now().toString(), "icon")

        dailyWaterDao.addDailyWater(item1)
        dailyWaterDao.addDailyWater(item2)
        dailyWaterDao.addDailyWater(item3)

        dailyWater = dailyWaterDao.getAllByDate(LocalDate.now().toString()).first()

        assertThat(dailyWater.size, equalTo(4))
    }


    @Test
    fun checkItemsSizeAfterDeletingEquals_3() = runTest{
        val item = DailyWater(null, 200, LocalTime.now().toString(), LocalDate.now().toString(), "icon")
        dailyWaterDao.addDailyWater(item)
        dailyWater = dailyWaterDao.getAllByDate(LocalDate.now().toString()).first()
        assertThat(dailyWater.size, equalTo(1))

        dailyWaterDao.delete(dailyWater[0])

        dailyWater = dailyWaterDao.getAllByDate(LocalDate.now().toString()).first()

        assertThat(dailyWater.size, equalTo(0))
    }

    @Test
    fun checkItemsEqualByMonthOctober() = runTest{
        val item1 = DailyWater(null, 200, LocalTime.of(7, 30).toString(), LocalDate.of(2023, 10, 3).toString(), "icon")
        val item2 = DailyWater(null, 300, LocalTime.of(10, 20).toString(), LocalDate.of(2023, 10, 3).toString(), "icon")
        val item3 = DailyWater(null, 150, LocalTime.of(15, 12).toString(), LocalDate.of(2023, 3, 4).toString(), "icon")

        dailyWaterDao.addDailyWater(item1)
        dailyWaterDao.addDailyWater(item2)
        dailyWaterDao.addDailyWater(item3)

        dailyWater = dailyWaterDao.getAllByMonth(yearMonthFormat(LocalDate.of(2023, 10, 11))).first()

        assertThat(dailyWater.size, equalTo(2))
    }

    @Test
    fun checkItemsEqualByMonthJanuary() = runTest{
        val item1 = DailyWater(null, 200, LocalTime.of(7, 30).toString(), LocalDate.of(2023, 10, 3).toString(), "icon")
        val item2 = DailyWater(null, 300, LocalTime.of(10, 20).toString(), LocalDate.of(2023, 10, 3).toString(), "icon")
        val item3 = DailyWater(null, 150, LocalTime.of(15, 12).toString(), LocalDate.of(2023, 1, 4).toString(), "icon")

        dailyWaterDao.addDailyWater(item1)
        dailyWaterDao.addDailyWater(item2)
        dailyWaterDao.addDailyWater(item3)

        dailyWater = dailyWaterDao.getAllByMonth(yearMonthFormat(LocalDate.of(2023, 1, 11))).first()

        assertThat(dailyWater.size, equalTo(1))
    }


}