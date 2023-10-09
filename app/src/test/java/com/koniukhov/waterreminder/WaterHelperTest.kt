package com.koniukhov.waterreminder

import com.koniukhov.waterreminder.data.user.Sex
import com.koniukhov.waterreminder.utilities.WaterHelper.Companion.calculateWaterAmount
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

class WaterHelperTest {
    @Test
    fun givenMaleWeight80_whenCalculateWaterAmount_Then3200() {
        assertThat(calculateWaterAmount(Sex.MALE, 80), equalTo(3200))
    }

    @Test
    fun givenFemaleWeight50_whenCalculateWaterAmount_Then1500() {
        assertThat(calculateWaterAmount(Sex.FEMALE, 50), equalTo(1500))
    }
}