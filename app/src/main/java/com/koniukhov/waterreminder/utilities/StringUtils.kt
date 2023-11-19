package com.koniukhov.waterreminder.utilities

import android.content.Context
import com.koniukhov.waterreminder.R

fun getPercentageOfWaterDrunk(amountOfWaterDrunk: Int, amountOfDailyLimit: Int): Int{
    val res = (amountOfWaterDrunk.toFloat() / amountOfDailyLimit) * 100

    return if(res <= 100) res.toInt() else 100
}

fun getStringPercentageOfWaterDrunk(amountOfWaterDrunk: Int, amountOfDailyLimit: Int, context: Context): String{
    val res = (amountOfWaterDrunk.toFloat() / amountOfDailyLimit) * 100

    return context.getString(R.string.remaining_percentage_today, res.toInt())
}

fun getStringAmountOfRemainingWater(amountOfWaterDrunk: Int, amountOfDailyLimit: Int, context: Context): String{
    var left = amountOfDailyLimit - amountOfWaterDrunk

    return when{
        left > 0 ->{
            context.getString(R.string.remaining_amount_today, left)
        }
        left == 0 ->{
            context.getString(R.string.goal_is_reached)
        }
        else ->{
            left *= -1
            context.getString(R.string.remaining_amount_more_today, left)
        }
    }
}