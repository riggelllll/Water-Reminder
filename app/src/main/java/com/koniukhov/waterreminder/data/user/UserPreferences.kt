package com.koniukhov.waterreminder.data.user

import java.time.LocalTime

data class UserPreferences(
    val weight: Int,
    val gender: Gender,
    val wakeUpTime: LocalTime,
    val bedTime: LocalTime,
    val isRemind: Boolean,
    val reminderInterval: Int,
    val waterLimitPerDay: Int,
    val isFirstOpening: Boolean
)
