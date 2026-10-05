package com.whitbread.premierinn.mybookings

import android.content.Context
import android.content.SharedPreferences
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
//import com.whitbread.premierinn.common.dagger.ComponentsManager
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_PASSWORD
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_USERNAME
import io.appflate.restmock.RESTMockServer
import io.appflate.restmock.utils.RequestMatchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MyBookingsTest {

    @Rule
    @JvmField
    var activityRule = IntentsTestRule<MyBookingsActivity>(MyBookingsActivity::class.java, true, false)
    private val username = "user"
    lateinit var context: Context
    lateinit var robot: MyBookingsRobot
    lateinit var sharedPrefs: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext

//        sharedPrefs = ComponentsManager.getInstance().appComponent.preferences()
//        editor = sharedPrefs.edit()

        robot = MyBookingsRobot()
    }

    @Test
    fun testClickCheckIn_prepaid_goesToNativeFlow() {
        setLoggedIn()
        RESTMockServer.whenGET(RequestMatchers.pathContains("/customers/hotels/$username/stays"))
                .thenReturnFile(200, "apiTest/retrieve-stays-prepaid-checkinavailable.json")

        activityRule.launchActivity(MyBookingsActivity.createIntent(context, false))

//        robot.clickCheckInOnBooking(0)
//                .successNativeCheckin()
    }

    private fun setLoggedIn() {
        editor.putString(KEY_USERNAME, username).apply()
        editor.putString(KEY_PASSWORD, "pass").apply()
    }
}