package com.whitbread.premierinn.hoteldetails

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.whitbread.premierinn.R
import com.whitbread.premierinn.utils.BaseRobot
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not

fun withRobot(func: HotelDetailsRobot.() -> Unit) = HotelDetailsRobot().apply { func() }

class HotelDetailsRobot : BaseRobot() {

    fun containsFlexRateBoxWithPrice(price: String) {
        //verifyContainsRateBox(R.id.hotel_details_rate_plan, R.id.standard_room_rates, "Flex", price)
    }

    fun containsSaverRateBoxWithPrice(price: String) {
        //verifyContainsRateBox(R.id.hotel_details_rate_plan, R.id.standard_room_rates, "Non-Flex", price)
    }

    fun containsAlternativeFlexRateBoxWithPrice(price: String) {
        //verifyContainsRateBox(R.id.hotel_details_rate_plan, R.id.alternative_room_rates, "Flex", price)
    }

    fun containsAlternativeSaverRateBoxWithPrice(price: String) {
        //verifyContainsRateBox(R.id.hotel_details_rate_plan, R.id.alternative_room_rates, "Non-Flex", price)
    }

    fun containsFirstRateBoxWithDescription(description: String) {
        //verifyRateDescription(R.id.hotel_details_rate_plan, R.id.standard_room_rates, description)
    }

    fun containsSecondRateBoxWithDescription(description: String) {
        //verifyRateDescription(R.id.hotel_details_rate_plan, R.id.standard_room_rates, description)
    }

    fun containsThirdRateBoxWithDescription(description: String) {
        //verifyRateDescription(R.id.hotel_details_rate_plan, R.id.standard_room_rates, description)
    }

    fun containsFirstRateBoxWithName(rateName: String) {
        //verifyRateName(R.id.hotel_details_rate_plan, R.id.standard_room_rates, rateName)
    }

    fun containsSecondRateBoxWithName(rateName: String) {
        //verifyRateName(R.id.hotel_details_rate_plan, R.id.standard_room_rates, rateName)
    }

    fun containsThirdRateBoxWithName(rateName: String) {
        //verifyRateName(R.id.hotel_details_rate_plan, R.id.standard_room_rates, rateName)
    }

    fun containsAlternativeFirstRateBoxWithName(rateName: String) {
        //verifyRateName(R.id.hotel_details_rate_plan, R.id.alternative_room_rates, rateName)
    }

    fun containsAlternativeSecondRateBoxWithName(rateName: String) {
        //verifyRateName(R.id.hotel_details_rate_plan, R.id.alternative_room_rates, rateName)
    }

    fun containsAlternativeThirdRateBoxWithName(rateName: String) {
        //verifyRateName(R.id.hotel_details_rate_plan, R.id.alternative_room_rates, rateName)
    }

    fun containsAlternativeFirstRateBoxWithDescription(description: String) {
       // verifyRateDescription(R.id.hotel_details_rate_plan, R.id.alternative_room_rates, description)
    }

    fun containsAlternativeSecondRateBoxWithDescription(description: String) {
        //verifyRateDescription(R.id.hotel_details_rate_plan, R.id.alternative_room_rates, description)
    }

    fun containsAlternativeThirdRateBoxWithDescription(description: String) {
        //verifyRateDescription(R.id.hotel_details_rate_plan, R.id.alternative_room_rates, description)
    }

    fun view_rates(position: Int) {
        scrollToRateView(position)
    }

    private fun verifyContainsRateBox(parentId: Int, grandParentId: Int, rateName: String, price: String) {
        onView(allOf(withId(R.id.price_box_rate_name), withParent(withId(parentId)), isDescendantOfA(withId(grandParentId))))
                .check(matches(withText(rateName)))
                .check(matches(isDisplayed()))
        onView(allOf(withId(R.id.tv_price_box_price), withParent(withId(parentId)), isDescendantOfA(withId(grandParentId))))
                .check(matches(withText(price)))
                .check(matches(isDisplayed()))
    }

    private fun verifyRateName(parentId: Int, grandParentId: Int, rateName: String) {
        onView(allOf(withId(R.id.price_box_rate_name), withParent(withId(parentId)), isDescendantOfA(withId(grandParentId))))
                .check(matches(withText(rateName)))
                .check(matches(isDisplayed()))
    }

    private fun verifyRateDescription(parentId: Int, grandParentId: Int, description: String) {
        onView(allOf(withId(R.id.tv_price_box_details), withParent(withId(parentId)), isDescendantOfA(withId(grandParentId))))
                .check(matches(withText(description)))
                .check(matches(isDisplayed()))
    }

    fun noRoomTypeTitleOrLearnMoreLink() {
        scrollToRateView(1)

//        onView(allOf(withId(R.id.hotel_details_rate_room_label), isDescendantOfA(withId(R.id.standard_room_rates))))
//                .check(matches(not(isDisplayed())))
//        onView(allOf(withId(R.id.hotel_details_rate_room_label), isDescendantOfA(withId(R.id.alternative_room_rates))))
//                .check(matches(not(isDisplayed())))
//        onView(allOf(withId(R.id.hotel_details_room_find_out_more), isDescendantOfA(withId(R.id.standard_room_rates))))
//                .check(matches(not(isDisplayed())))
//        onView(allOf(withId(R.id.hotel_details_room_find_out_more), isDescendantOfA(withId(R.id.alternative_room_rates))))
//                .check(matches(not(isDisplayed())))
    }

    fun noAlternativeRoomUpsells() {
        //view(R.id.alternative_room_rates).check(matches(not(isDisplayed())))
    }

    fun scrollToRateView(pos: Int = 1) {
        view(R.id.hotel_details_content_list)
                .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(pos, scrollTo()))
    }

    fun hasStandardRoomsTitleAndLearnMore(pos: Int = 1) {
        scrollToRateView(pos)
        //verifyRatesGroupTitleAndLearnMore("Standard rooms", R.id.standard_room_rates)
    }

    fun hasBusinessRoomsTitleAndLearnMore() {
        scrollToRateView(1)
        //verifyRatesGroupTitleAndLearnMore("Business rooms", R.id.alternative_room_rates)
    }

    private fun verifyRatesGroupTitleAndLearnMore(title: String, parentId: Int) {
        onView(allOf(withId(R.id.hotel_details_rate_room_label), isDescendantOfA(withId(parentId))))
                .check(matches(withText(title)))
        onView(allOf(withId(R.id.hotel_details_room_find_out_more), isDescendantOfA(withId(parentId))))
                .check(matches(isDisplayed()))
    }

    fun verifyAccessibleInfoPresent() {
        assertEnabled(R.id.reception_info)
        assertEnabled(R.id.call_description)
        assertEnabled(R.id.call_btn_text)
        assertEnabled(R.id.email_btn)
        assertEnabled(R.id.accessible_room_type_info_btn)
        assertEnabled(R.id.accessibility_info_btn)
    }

    fun verifyAccessibleInfoNotPresent() {
        assertNotVisible(R.id.reception_info)
        assertNotVisible(R.id.call_description)
        assertNotVisible(R.id.call_btn_text)
        assertNotVisible(R.id.email_btn)
        assertNotVisible(R.id.accessible_room_type_info_btn)
        assertNotVisible(R.id.accessibility_info_btn)
    }

    fun verifyFlagTextShown(text: String) {
        assertVisible(R.id.hotel_details_color_message)
        matchText(R.id.hotel_details_color_message, text)
    }

    fun verifyFlagTextNotShown() {
        assertNotVisible(R.id.hotel_details_color_message)
    }

    fun verifyLastFewRoomsLabelShown() {
        assertVisible(R.id.hotel_details_last_few_rooms)
        matchText(R.id.hotel_details_last_few_rooms, "Last few rooms")
    }

    fun verifyLastFewRoomsLabelNotShown() {
        assertNotVisible(R.id.hotel_details_last_few_rooms)
    }

    fun verifySubstitutionPanelShownContainingText(vararg text: String) {
        assertVisible(R.id.tv_important_hotel_room_substitution_info_panel)
        assertVisible(R.id.room_substitution_info)
        for (string in text) {
            containsText(R.id.room_substitution_info, string)
        }
    }

    fun verifySubstitutionPanelNotShown() {
        assertNotVisible(R.id.tv_important_hotel_room_substitution_info_panel)
    }

    fun verifyTripAdvisorRatingShown(rating: String) {
        assertVisible(R.id.hotel_details_trip_advisor)
        assertHasChildWithText(R.id.hotel_details_trip_advisor, rating)
    }

    fun verifyHotelDetailPage(hotelName : String) = apply {
        matchText(R.id.hotel_details_hotel_name, hotelName)
                .check(matches(isDisplayed()))
    }

    fun verifyHotelDetailToolbar(hotelName : String) = apply {
        matchText(R.id.hotel_details_toolbar_title, hotelName)
                .check(matches(isDisplayed()))
    }

    // Note: This will fail if we provide a different json structure
    fun chooseStandardRate(rateName: String) {
        val id = when (rateName) {
            "Flex" -> R.id.hotel_details_rate_plan
            "Non-Flex" -> R.id.hotel_details_rate_plan
            else -> R.id.hotel_details_rate_plan
        }

        val pos = when (rateName) {
            "Flex" -> 1
            "Non-Flex" -> 2
            else -> 0
        }

        scrollToListItem<RecyclerView.ViewHolder>(R.id.hotel_details_content_list, pos)

        val buttonMatcher = allOf(withText("Book"),
                //isDescendantOfA(withId(R.id.standard_room_rates)),
                isDescendantOfA(withId(id)))

        onView(buttonMatcher).perform(click())
    }

    fun chooseRate(position: Int) {
        val id = when (position) {
            1 -> R.id.hotel_details_rate_plan
            2 -> R.id.hotel_details_rate_plan
            else -> R.id.hotel_details_rate_plan
        }
        scrollToListItem<RecyclerView.ViewHolder>(R.id.hotel_details_content_list, position)

        val buttonMatcher = allOf(withText("Book"),
                //isDescendantOfA(withId(R.id.standard_room_rates)),
                isDescendantOfA(withId(id)))

        onView(buttonMatcher).perform(click())
    }

    fun chooseAlternativeRate(position: Int) {
        val id = when (position) {
            1 -> R.id.hotel_details_rate_plan
            2 -> R.id.hotel_details_rate_plan
            else -> R.id.hotel_details_rate_plan
        }

        val buttonMatcher = allOf(withText("Book"),
                //isDescendantOfA(withId(R.id.alternative_room_rates)),
                isDescendantOfA(withId(id)))

        onView(buttonMatcher).perform(click())
    }

    fun verifyFlexAvailability(position: Int) {
        val id = when (position) {
            1 -> R.id.hotel_details_rate_plan
            2 -> R.id.hotel_details_rate_plan
            else -> R.id.hotel_details_rate_plan
        }

        allOf(withText("Book"),
                //isDescendantOfA(withId(R.id.standard_room_rates)),
                isDescendantOfA(withId(id))).matches(isDisplayed())
    }

    fun verifyAlternativeSemiFlexAvailability(position: Int) {
        val id = when (position) {
            1 -> R.id.hotel_details_rate_plan
            2 -> R.id.hotel_details_rate_plan
            else -> R.id.hotel_details_rate_plan
        }

        allOf(withText("Book"),
                //isDescendantOfA(withId(R.id.alternative_room_rates)),
                isDescendantOfA(withId(id))).matches(isDisplayed())
    }

    infix fun verifyRates(func: HotelDetailsRobot.() -> Unit) = func()

    infix fun holdBooking(func: HoldBookingResult.() -> Unit): HoldBookingResult {
        //click view and....
        return HoldBookingResult().apply { func() }
    }

    inner class HoldBookingResult {
        fun success() {
            //TODO
        }
    }
}