package com.whitbread.premierinn.landing;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.appflate.restmock.RESTMockServer;

@RunWith(AndroidJUnit4.class)
public class LandingActivityTest {

    public @Rule ActivityTestRule<LandingActivity> activityTestRule = new ActivityTestRule<>(LandingActivity.class, true, false);
    //TestPIApplication context;

    @Before
    public void setUp() throws Exception {
        //context = (TestPIApplication) getInstrumentation().getTargetContext().getApplicationContext();

        RESTMockServer.reset();
    }

    @Test
    public void welcomeMessageOk() throws Exception {

        //activityTestRule.launchActivity(LandingActivityIntent.INSTANCE.create(context));

        onView(withText("Where to?")).check(matches(isDisplayed()));
    }
}
