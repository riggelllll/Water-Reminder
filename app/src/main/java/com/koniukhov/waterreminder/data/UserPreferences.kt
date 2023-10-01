package com.koniukhov.waterreminder.data

import java.time.LocalTime

data class UserPreferences(
    val weight: Int,
    val sex: Sex,
    val wakeUpTime: LocalTime,
    val bedTime: LocalTime,
    val isRemind: Boolean,
    val reminderInterval: Int,
    val waterLimitPerDay: Int,
    val isFirstOpening: Boolean
)
