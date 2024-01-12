package com.koniukhov.waterreminder

import com.koniukhov.waterreminder.utilities.isBetweenLocalTime
import com.koniukhov.waterreminder.utilities.yearMonthFormat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalTime

class TimeUtilsTest {

    @Test
    fun isBetweenTimeTimeBetween6and22True(){
        val now = LocalTime.of(10, 0)
        val wakeUp = LocalTime.of(6, 0)
        val bedTime = LocalTime.of(22, 0 )

        assertTrue(isBetweenLocalTime(now, wakeUp, bedTime))
    }

    @Test
    fun isBetweenTimeTimeBetween12and28True(){
        val now = LocalTime.of(13, 30)
        val wakeUp = LocalTime.of(12, 30)
        val bedTime = LocalTime.of(18, 0 )

        assertTrue(isBetweenLocalTime(now, wakeUp, bedTime))
    }

    @Test
    fun isBetweenTimeTimeBetween12and28False(){
        val now = LocalTime.of(9, 45)
        val wakeUp = LocalTime.of(12, 30)
        val bedTime = LocalTime.of(18, 0 )

        assertFalse(isBetweenLocalTime(now, wakeUp, bedTime))
    }

    @Test
    fun yearMonthFormatShouldBe_2024_12(){
        val yearMonth = yearMonthFormat(LocalDate.of(2024,12,11))
        assertEquals("2024-12", yearMonth)
    }

    @Test
    fun yearMonthFormatShouldBe_2024_01(){
        val yearMonth = yearMonthFormat(LocalDate.now())
        assertEquals("2024-01", yearMonth)
    }

    @Test
    fun yearMonthFormatSizeShouldBeEqual_7(){
        var startDate = LocalDate.of(2024,1,1)
        for (i in 0..365){
            val yearMonth = yearMonthFormat(startDate)
            assertEquals(7, yearMonth.length)
            startDate = startDate.plusDays(1)
        }
    }

    @Test
    fun yearMonthFormatShouldBeJanuary(){
        var startDate = LocalDate.of(2024,1,1)
        for (i in 0..25){
            val yearMonth = yearMonthFormat(startDate)
            assertEquals("2024-01", yearMonth)
            startDate = startDate.plusDays(1)
        }
    }

    @Test
    fun yearMonthFormatShouldBeMarch(){
        var startDate = LocalDate.of(2024,3,1)
        for (i in 0..25){
            val yearMonth = yearMonthFormat(startDate)
            assertEquals("2024-03", yearMonth)
            startDate = startDate.plusDays(1)
        }
    }

    @Test
    fun yearMonthFormatShouldBeDecember(){
        var startDate = LocalDate.of(2024,12,1)
        for (i in 0..25){
            val yearMonth = yearMonthFormat(startDate)
            assertEquals("2024-12", yearMonth)
            startDate = startDate.plusDays(1)
        }
    }
}