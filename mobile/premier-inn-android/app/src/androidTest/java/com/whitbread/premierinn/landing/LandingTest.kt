package com.whitbread.premierinn.landing

import android.content.Context
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import io.appflate.restmock.RESTMockServer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LandingTest {

    @Rule
    @JvmField
    var activityRule = ActivityTestRule<LandingActivity>(LandingActivity::class.java, true, false)

    lateinit var context: Context

    @Before
    @Throws(Exception::class)
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        RESTMockServer.reset()
    }

    @Test
    @Throws(Exception::class)
    fun welcomeMessageOkFromServer() {
        activityRule.launchActivity(LandingActivityIntent.create(context))

        onView(withText("Where to?")).check(matches(isDisplayed()))
    }
}