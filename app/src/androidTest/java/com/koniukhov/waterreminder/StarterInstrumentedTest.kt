package com.koniukhov.waterreminder


import android.content.Context
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.koniukhov.waterreminder.data.user.Sex
import com.koniukhov.waterreminder.data.user.UserDataStore
import com.koniukhov.waterreminder.data.user.UserPreferences
import com.koniukhov.waterreminder.data.user.dataStore
import com.koniukhov.waterreminder.fragments.StarterFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalTime


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class StarterInstrumentedTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private val testContext: Context =
        InstrumentationRegistry.getInstrumentation().targetContext
    private val myDataStore: UserDataStore = UserDataStore(testContext.dataStore)

    private val expectedCreatedUserPreferences: UserPreferences = UserPreferences(
        60,
        Sex.FEMALE,
        LocalTime.of(6, 0),
        LocalTime.of(22,0),
        true,
        reminderInterval = 2,
        waterLimitPerDay = 1800,
        isFirstOpening = false
    )


    @Test
    fun bottomNavigationShouldBeInvisible() {
        onView(withId(R.id.bottom_navigation)).check(matches(not(isDisplayed())))
    }

    @Test
    fun testEventFragment() = runTest {
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext())

        val scenario = launchFragmentInContainer(themeResId = R.style.Theme_WaterReminder) {
            StarterFragment().also { fragment ->

                fragment.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
                    if (viewLifecycleOwner != null) {
                        navController.setGraph(R.navigation.nav_graph)
                        Navigation.setViewNavController(fragment.requireView(), navController)
                        navController.setCurrentDestination(R.id.starterFragment)

                    }
                }
            }
        }

        onView(withId(R.id.next_button)).perform(click())
        onView(withId(R.id.layout_female)).perform(click())
        onView(withId(R.id.next_button)).perform(click())
        onView(withId(R.id.next_button)).perform(click())
        onView(withId(R.id.next_button)).perform(click())
        assertThat(navController.currentDestination?.id, equalTo(R.id.homeFragment))

        val currentPreferences: UserPreferences = myDataStore.userPreferencesFlow.first()
        assertThat(currentPreferences, equalTo(expectedCreatedUserPreferences))
    }
}