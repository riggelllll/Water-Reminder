package com.koniukhov.waterreminder

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.koniukhov.waterreminder.data.user.Sex
import com.koniukhov.waterreminder.data.user.UserDataStore
import com.koniukhov.waterreminder.data.user.UserPreferences
import com.koniukhov.waterreminder.data.user.dataStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalTime

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class UserDataStoreTest {

    private val expectedInitialUserPreferences: UserPreferences = UserPreferences(
        0,
        Sex.MALE,
        LocalTime.of(0, 0),
        LocalTime.of(0,0),
        false,
        reminderInterval = 2,
        waterLimitPerDay = 0,
        isFirstOpening = true
    )
    private val expectedCreatedUserPreferences: UserPreferences = UserPreferences(
        80,
        Sex.MALE,
        LocalTime.of(6, 0),
        LocalTime.of(22,0),
        true,
        reminderInterval = 2,
        waterLimitPerDay = 1500,
        isFirstOpening = false
    )

    private val expectedChangedUserPreferences: UserPreferences = UserPreferences(
        0,
        Sex.MALE,
        LocalTime.of(0, 0),
        LocalTime.of(0,0),
        false,
        reminderInterval = 4,
        waterLimitPerDay = 2000,
        isFirstOpening = true
    )

    private val testContext: Context =
        InstrumentationRegistry.getInstrumentation().targetContext
    private val myDataStore: UserDataStore = UserDataStore(testContext.dataStore)


    @After
    fun cleanUp() {
        runTest {
            testContext.dataStore.edit { it.clear() }
        }
    }

    @Test
    fun testInitialPreferences() = runTest {
        val currentPreferences: UserPreferences = myDataStore.userPreferencesFlow.first()
        assertThat(currentPreferences, equalTo(expectedInitialUserPreferences))
    }

    @Test
    fun testCreatedUser() = runTest {
        myDataStore.updateWeight(80)
        myDataStore.updateSex(0)
        myDataStore.updateWakeUpTime(6, 0)
        myDataStore.updateBedTime(22, 0)
        myDataStore.updateIsRemind(true)
        myDataStore.updateReminderInterval(2)
        myDataStore.updateWaterLimitPerDay(1500)
        myDataStore.updateIsFirstOpening(false)

        val currentPreferences: UserPreferences = myDataStore.userPreferencesFlow.first()
        assertThat(currentPreferences, equalTo(expectedCreatedUserPreferences))
    }

    @Test
    fun testChangedUser() = runTest {
        myDataStore.updateReminderInterval(4)
        myDataStore.updateWaterLimitPerDay(2000)

        val currentPreferences: UserPreferences = myDataStore.userPreferencesFlow.first()
        assertThat(currentPreferences, equalTo(expectedChangedUserPreferences))
    }

    @Test
    fun testSaveUser() = runTest {

        myDataStore.saveUser(80, Sex.MALE, LocalTime.of(6,0),
            LocalTime.of(22, 0),1500)

        val currentPreferences: UserPreferences = myDataStore.userPreferencesFlow.first()
        assertThat(currentPreferences, equalTo(expectedCreatedUserPreferences))
    }
}