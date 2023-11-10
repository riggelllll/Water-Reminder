package com.koniukhov.waterreminder

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.koniukhov.waterreminder.utilities.getPercentageOfWaterDrunk
import com.koniukhov.waterreminder.utilities.getStringAmountOfRemainingWater
import com.koniukhov.waterreminder.utilities.getStringPercentageOfWaterDrunk
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StringUtilsTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    @Test
    fun percentageMustBe10(){
        val res = getPercentageOfWaterDrunk(100, 1000)
        MatcherAssert.assertThat(res, CoreMatchers.equalTo(10))
    }

    @Test
    fun percentageMustBe0(){
        val res = getPercentageOfWaterDrunk(0, 1000)
        MatcherAssert.assertThat(res, CoreMatchers.equalTo(0))
    }

    @Test
    fun percentageMustBe150(){
        val res = getPercentageOfWaterDrunk(1500, 1000)
        MatcherAssert.assertThat(res, CoreMatchers.equalTo(100))
    }

    @Test
    fun percentageWithTextMustBe150(){
        val res = getStringPercentageOfWaterDrunk(1500, 1000, context)
        MatcherAssert.assertThat(res, CoreMatchers.equalTo("150% Today"))
    }

    @Test
    fun percentageWithTextMustBe20(){
        val res = getStringPercentageOfWaterDrunk(640, 3200, context)
        MatcherAssert.assertThat(res, CoreMatchers.equalTo("20% Today"))
    }

    @Test
    fun remainingWaterMustBe500(){
        val res = getStringAmountOfRemainingWater(4000, 4500, context)
        MatcherAssert.assertThat(res, CoreMatchers.equalTo("500 ml left to reach the goal"))
    }

    @Test
    fun remainingWaterMustBe0(){
        val res = getStringAmountOfRemainingWater(4000, 4000, context)
        MatcherAssert.assertThat(res, CoreMatchers.equalTo("You have reached your goal"))
    }

    @Test
    fun remainingWaterMustBe200More(){
        val res = getStringAmountOfRemainingWater(4200, 4000, context)
        MatcherAssert.assertThat(res, CoreMatchers.equalTo("200 ml more than needed"))
    }


}