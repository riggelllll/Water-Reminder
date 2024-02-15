package com.koniukhov.waterreminder

import com.koniukhov.waterreminder.data.user.Gender
import com.koniukhov.waterreminder.utilities.Constants
import com.koniukhov.waterreminder.utilities.WaterHelper
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class WaterLimitTest {

    @Test
    fun waterLimitForMenShouldBeDivisibleByStepWithoutReminder(){
        for (weight in Constants.WEIGHT_MIN .. Constants.WEIGHT_MAX){
            assertTrue(WaterHelper.calculateWaterAmount(Gender.MALE, weight) % Constants.WATER_LIMIT_STEP == 0)
        }
    }

    @Test
    fun waterLimitForWomenShouldBeDivisibleByStepWithoutReminder(){
        for (weight in Constants.WEIGHT_MIN .. Constants.WEIGHT_MAX){
            assertTrue(WaterHelper.calculateWaterAmount(Gender.FEMALE, weight) % Constants.WATER_LIMIT_STEP == 0)
        }
    }
}