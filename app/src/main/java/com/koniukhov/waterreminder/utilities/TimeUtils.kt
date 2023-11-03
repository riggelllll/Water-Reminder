package com.koniukhov.waterreminder.utilities

import java.time.LocalDate
import java.time.LocalTime

fun isBetweenLocalTime(now: LocalTime, start: LocalTime, end: LocalTime): Boolean{
    return now.isAfter(start) && now.isBefore(end)
}

fun yearMonthFormat(date: LocalDate): String{
    return "" + date.year + "-" + date.monthValue
}