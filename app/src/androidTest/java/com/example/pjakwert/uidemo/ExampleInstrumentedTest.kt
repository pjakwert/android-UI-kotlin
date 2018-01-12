package com.example.pjakwert.uidemo

import android.app.PendingIntent.getActivity
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.support.test.espresso.Espresso.*
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.test.ActivityInstrumentationTestCase2
import org.hamcrest.Matchers

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@RunWith(AndroidJUnit4::class)
@LargeTest
class ExampleInstrumentedTest : ActivityInstrumentationTestCase2<MainActivity>(MainActivity::class.java){



    @Rule @JvmField
    val activity : ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)


    @Before
    public override fun setUp() {
        super.setUp()
                //getActivity()
    }


    @Test
    public fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("com.example.pjakwert.uidemo", appContext.packageName)
    }

    @Test
    public fun navigationViewIsVisibleAfterClick() {
        //mActivityRule.launchActivity(null)
        onView(withClassName( Matchers.equalToIgnoringCase("AppCompatImageButton"))).perform(click())
        onView(withId(R.id.design_navigation_view)).check(matches(isDisplayed()))
    }
}
