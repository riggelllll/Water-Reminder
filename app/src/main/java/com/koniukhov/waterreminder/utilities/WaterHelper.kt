package com.koniukhov.waterreminder.utilities

import com.koniukhov.waterreminder.data.user.Gender
import com.koniukhov.waterreminder.utilities.Constants.FEMALE_COEFFICIENT
import com.koniukhov.waterreminder.utilities.Constants.MALE_COEFFICIENT

object WaterHelper{

    fun calculateWaterAmount(gender: Gender, weight: Int): Int{
        return if (gender == Gender.MALE){
            weight * MALE_COEFFICIENT
        }else{
            weight * FEMALE_COEFFICIENT
        }
    }
}