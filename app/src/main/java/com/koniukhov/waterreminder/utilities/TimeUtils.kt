package com.koniukhov.waterreminder.utilities

import java.time.LocalDate
import java.time.LocalTime

fun isBetweenLocalTime(now: LocalTime, start: LocalTime, end: LocalTime): Boolean{
    return now.isAfter(start) && now.isBefore(end)
}

/**
 * Returns the string equals format Year-Month ex (2024-01).
 *
 * @param date current date.
 */
fun yearMonthFormat(date: LocalDate): String{
    return date.toString().substring(0, 7)
}