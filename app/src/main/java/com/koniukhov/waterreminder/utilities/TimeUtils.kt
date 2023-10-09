package com.koniukhov.waterreminder.utilities

import java.time.LocalTime

fun isBetweenLocalTime(now: LocalTime, start: LocalTime, end: LocalTime): Boolean{
    return now.isAfter(start) && now.isBefore(end)
}