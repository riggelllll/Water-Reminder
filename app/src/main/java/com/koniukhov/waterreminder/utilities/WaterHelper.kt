package com.koniukhov.waterreminder.utilities

import com.koniukhov.waterreminder.data.user.Gender

class WaterHelper {
    companion object{
        private const val MALE_COEFFICIENT = 40
        private const val FEMALE_COEFFICIENT = 30

        fun calculateWaterAmount(gender: Gender, weight: Int): Int{
            return if (gender == Gender.MALE){
                weight * MALE_COEFFICIENT
            }else{
                weight * FEMALE_COEFFICIENT
            }
        }
    }

}