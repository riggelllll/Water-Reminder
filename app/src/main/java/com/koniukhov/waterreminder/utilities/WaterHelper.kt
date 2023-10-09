package com.koniukhov.waterreminder.utilities

import com.koniukhov.waterreminder.data.user.Sex

class WaterHelper {
    companion object{
        private const val MALE_COEFFICIENT = 40
        private const val FEMALE_COEFFICIENT = 30

        fun calculateWaterAmount(sex: Sex, weight: Int): Int{
            return if (sex == Sex.MALE){
                weight * MALE_COEFFICIENT
            }else{
                weight * FEMALE_COEFFICIENT
            }
        }
    }

}