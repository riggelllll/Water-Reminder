package com.koniukhov.waterreminder

import com.koniukhov.waterreminder.utilities.isBetweenLocalTime
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
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
}