package com.koniukhov.waterreminder

import com.koniukhov.waterreminder.data.user.Gender
import com.koniukhov.waterreminder.utilities.WaterHelper.calculateWaterAmount
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

class WaterHelperTest {
    @Test
    fun givenMaleWeight80_whenCalculateWaterAmount_Then3200() {
        assertThat(calculateWaterAmount(Gender.MALE, 80), equalTo(3200))
    }

    @Test
    fun givenFemaleWeight50_whenCalculateWaterAmount_Then1500() {
        assertThat(calculateWaterAmount(Gender.FEMALE, 50), equalTo(1500))
    }
}