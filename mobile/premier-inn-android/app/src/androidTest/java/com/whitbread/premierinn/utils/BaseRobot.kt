package com.whitbread.premierinn.utils

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.hasSibling
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.isNotChecked
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withSubstring
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.whitbread.premierinn.domain.countries.entity.CountryDomain
import com.whitbread.premierinn.mybookings.BookingUiModelViewHolder
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Assert


open class BaseRobot {

    fun view(resId: Int): ViewInteraction = onView(withId(resId))

    fun view(value: String): ViewInteraction = onView(withText(value))

    fun viewTextWithSiblingText(value: String, siblingValue: String): ViewInteraction = onView(allOf(withText(value), hasSibling(withText(siblingValue))))

    fun viewTextDescendantFromView(viewId: Int, descendantMatcher: Matcher<View>): ViewInteraction = onView(allOf(isDescendantOfA(descendantMatcher), withId(viewId)))

    fun scrollToView(resId: Int): ViewInteraction = scrollToView(view(resId))

    fun scrollToView(value: String): ViewInteraction = scrollToView(view(value))

    fun scrollToView(view: ViewInteraction): ViewInteraction = view.perform(scrollTo())

    fun selectItemFromListAt(resId: Int, pos: Int = 1): ViewInteraction = view(resId).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(pos, ViewActions.click()))

    private fun <T : RecyclerView.ViewHolder> scrollToRecyclerViewPosition(recyclerViewId: Int, position: Int): ViewInteraction {
        return view(recyclerViewId).perform(RecyclerViewActions.scrollToPosition<T>(position))
    }

    fun fillEditText(resId: Int, text: String): ViewInteraction = fillEditText(view(resId), text)

    fun fillEditText(viewInteraction: ViewInteraction, text: String): ViewInteraction =
            viewInteraction.perform(ViewActions.replaceText(text), ViewActions.closeSoftKeyboard())

    fun clickView(resId: Int): ViewInteraction = view(resId).perform(scrollTo(), ViewActions.click())

    fun clickView(text: String): ViewInteraction = view(text).perform(scrollTo(), ViewActions.click())

    private fun matchText(viewInteraction: ViewInteraction, text: String): ViewInteraction = viewInteraction
            .check(matches(withText(text)))

    fun matchText(resId: Int, text: String): ViewInteraction = matchText(view(resId), text)

    fun assertHasChildWithText(resId: Int, text: String): ViewInteraction = view(resId).check(matches(hasDescendant(withText(text))))

    fun containsText(resId: Int, value: String): ViewInteraction = view(resId).check(matches(withSubstring(value)))

    fun assertEnabled(resId: Int): ViewInteraction = view(resId).check(matches(isEnabled()))

    fun assertDisabled(resId: Int): ViewInteraction = view(resId).check(matches(not(isEnabled())))

    fun assertVisible(resId: Int): ViewInteraction = onView(withId(resId)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

    fun assertNotVisible(resId: Int): ViewInteraction = onView(withId(resId)).check { view, _ ->
        view?.let {
            Assert.assertTrue("View with id R.id.${view.resources.getResourceEntryName(resId)} should not be visible", !it.isVisible)
        }
    }

    fun toggleCheckboxOn(resId: Int): ViewInteraction = onView(withId(resId)).check(matches(isNotChecked()))
            .perform(ViewActions.click())

    fun <T : RecyclerView.ViewHolder> scrollToListItem(listId: Int, listIndex: Int): ViewInteraction {
        return scrollToRecyclerViewPosition<T>(listId, listIndex)
    }

    fun clickViewInListItem(listId: Int, listIndex: Int, viewIdToClick: Int) {
        onView(withId(listId))
                .perform(RecyclerViewActions
                        .actionOnItemAtPosition<BookingUiModelViewHolder>(listIndex, clickChildViewWithId(viewIdToClick)))
    }

    fun selectCountryInCountryList(countryName: String, countryListResId: Int) {
        Espresso.onData(countryNameContains(countryName))
                .inAdapterView(withId(countryListResId))
                .check(matches(ViewMatchers.isDisplayed()))
    }

    private fun countryNameContains(countryName: String): Matcher<CountryDomain> {
        return CountryMatcher(countryName)
    }

    private class CountryMatcher(private val countryName: String) : TypeSafeMatcher<CountryDomain>() {
        override fun describeTo(description: Description) {

        }

        override fun matchesSafely(item: CountryDomain): Boolean {
            return item.countryName.contains(countryName)
        }
    }

    fun childAtPosition(parentMatcher: Matcher<View>, position: Int): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {

            }

            override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}