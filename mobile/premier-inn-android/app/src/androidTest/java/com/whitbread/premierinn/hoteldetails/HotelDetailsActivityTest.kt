package com.whitbread.premierinn.hoteldetails

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.whitbread.premierinn.searchresults.SearchResultsInput
import io.appflate.restmock.RESTMockServer
import io.appflate.restmock.utils.RequestMatchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDate


@RunWith(AndroidJUnit4::class)
class HotelDetailsActivityTest {

    @Rule
    @JvmField
    val activityRule = ActivityTestRule(HotelDetailsActivity::class.java, true, false)

    lateinit var intentInput: HotelDetailsInput
    private val robot = HotelDetailsRobot()

    @Test
    fun should_just_show_standard_rates_1() {
        // given Request of 1 adult - 1 DB Room - for 1 night - on 23/08/2019 for LONBLA
        //       and Availability OK - Standard Rooms - 2 Rate (Flex, Non-Flex)

        // cases:
        //  Standard Availability - Single room
        //  Standard Availability - Multiple rooms
        //  Standard Availability - Multiple rooms (1 Accessible)
        //  Standard Availability with Room Upgrade option (Premium | Business | Bigger) - Single room
        //  Standard Availability with Room Upgrade option (Premium | Business | Bigger) - Multiple rooms
        //  Standard Availability with Room Upgrade option (Premium | Business | Bigger) - Multiple rooms (1 Accessible)
        //  Silent Sub Any RoomType but accessible
        //  Non-Silent Sub = ACCESSIBLE
        //  Alternative ACCESSIBLE BATHTYPES AND ROOM TYPES - NOTHING TO SHOW HERE

        intentInput = createInputIntent("LONBLA", listOf("DB"))
        stubApiCallsWith("LONBLA", "availability-1room-standard")
        launchScreen(intentInput)

        withRobot {
            // verifying state in groups - Group by component
            verifyRates {
                hasStandardRoomsTitleAndLearnMore()
                containsFlexRateBoxWithPrice(price = "£93.00")
                containsSaverRateBoxWithPrice(price = "£70.00")
                noAlternativeRoomUpsells()
            }
//            any actions that might cause navigation to other screens ..etc
//            holdBooking {
//                success()
//            }
        }
    }

    @Test
    fun should_just_show_standard_rates_2() {
        intentInput = createInputIntent("LONBLA", listOf("DB"))
        stubApiCallsWith("LONBLA", "availability-4room-standard")
        launchScreen(intentInput)

        withRobot {
            hasStandardRoomsTitleAndLearnMore()
            containsFlexRateBoxWithPrice(price = "£552.00")
            containsSaverRateBoxWithPrice(price = "£414.00")
            noAlternativeRoomUpsells()
        }

    }

    @Test
    fun should_just_show_standard_rates_no_tile() {
        intentInput = createInputIntent("LONBLA", listOf("DB"))
        stubApiCallsWith("LONBLA", "availability-2room-standard-with-accessible")
        launchScreen(intentInput)

        withRobot {
            noRoomTypeTitleOrLearnMoreLink()
            containsFlexRateBoxWithPrice(price = "£276.00")
            containsSaverRateBoxWithPrice(price = "£207.00")
            noAlternativeRoomUpsells()
        }
    }

    @Test
    fun should_show_business_and_standard_rates_multiple_rooms_nights() {
        intentInput = createInputIntent("LONBLA", listOf("DB"))
        stubApiCallsWith("LONBLA", "availability-2room-2night-business-upsell")
        launchScreen(intentInput)

        withRobot {

            verifyRates {
                hasBusinessRoomsTitleAndLearnMore()
                containsAlternativeFlexRateBoxWithPrice(price = "£264.00")
                containsAlternativeSaverRateBoxWithPrice(price = "£232.00")
            }

            verifyRates {
                hasStandardRoomsTitleAndLearnMore(2)
                containsFlexRateBoxWithPrice(price = "£244.00")
                containsSaverRateBoxWithPrice(price = "£212.00")
            }
        }
    }

    @Test
    fun should_not_show_accessibility_info_panel() {
        intentInput = createInputIntent("LONBLA", listOf("DB"))
        stubApiCallsWith("LONBLA", "availability-1room-standard")
        launchScreen(intentInput)

        withRobot {
            verifyAccessibleInfoNotPresent()
        }
    }

    @Test
    fun should_show_accessibility_info_panel() {
        intentInput = createInputIntent("LONBLA", listOf("DB", "DIS"))
        stubApiCallsWith("LONBLA", "availability-1room-standard")
        launchScreen(intentInput)

        withRobot {
            verifyAccessibleInfoPresent()
        }
    }

    @Test
    fun should_show_flag_text_label() {
        intentInput = createInputIntent("WATTGI", listOf("DB"))
        stubApiCallsWith("WATTGI", "availability-1room-standard")
        launchScreen(intentInput)

        withRobot {
            verifyFlagTextShown("New rooms")
        }
    }

    @Test
    fun should_not_show_flag_text_label() {
        intentInput = createInputIntent("LONBLA", listOf("DB"))
        stubApiCallsWith("LONBLA", "availability-1room-standard")
        launchScreen(intentInput)

        withRobot {
            verifyFlagTextNotShown()
        }
    }

    @Test
    fun should_show_last_few_rooms_label() {
        intentInput = createInputIntent("LONBLA", listOf("DB"))
        stubApiCallsWith("LONBLA", "availability-1room-standard")
        launchScreen(intentInput)

        with(robot) {
            verifyLastFewRoomsLabelShown()
        }
    }

    @Test
    fun should_not_show_last_few_rooms_label() {
        intentInput = createInputIntent("WATTGI", listOf("DB"))
        stubApiCallsWith("WATTGI", "availability-1room-standard")
        launchScreen(intentInput)

        with(robot) {
            verifyLastFewRoomsLabelNotShown()
        }
    }

    @Test
    fun should_show_substitution_message() {
        intentInput = createInputIntent("LIVSHI", listOf("DB"))
        stubApiCallsWith("LIVSHI", "availability_1room-standard_substituted")
        launchScreen(intentInput)

        with(robot) {
            verifySubstitutionPanelShownContainingText(
                    "We've had to make some changes to your room type",
                    "We don't have any double rooms available at this hotel, so we've changed your room type to a accessible room")
        }
    }

    @Test
    fun with_non_silent_substitution_should_not_show_accessibility_panel() {
        intentInput = createInputIntent("LIVSHI", listOf("DB"))
        stubApiCallsWith("LIVSHI", "availability_1room-standard_substituted")
        launchScreen(intentInput)

        with(robot) {
            verifyAccessibleInfoNotPresent()
        }
    }

    @Test
    fun should_not_show_substitution_message() {
        intentInput = createInputIntent("LONBLA", listOf("DB"))
        stubApiCallsWith("LONBLA", "availability-1room-standard")
        launchScreen(intentInput)

        with(robot) {
            verifySubstitutionPanelNotShown()
        }
    }

    @Test
    fun should_show_trip_advisor_rating() {
        intentInput = createInputIntent("LONBLA", listOf("DB"));
        stubApiCallsWith("LONBLA", "availability-1room-standard")
        launchScreen(intentInput)

        with(robot) {
            verifyTripAdvisorRatingShown("5264 reviews")
        }
    }

    // add test cases for silent upsell/not silent sub, accessible with alt bath and multiple upselltypes edge cases

    private fun stubApiCallsWith(hotelCode: String, suffix: String) {
        RESTMockServer.whenGET(RequestMatchers.pathContains("/booking/hotels/${hotelCode}/availability"))
                .thenReturnFile(200, "apiTest/booking/hotels/${hotelCode}/$suffix.json")

        RESTMockServer.whenGET(RequestMatchers.pathEndsWith("/hotels/${hotelCode}"))
                .thenReturnFile(200, "apiTest/hotels/${hotelCode}.json")
    }

    private fun launchScreen(input: HotelDetailsInput) {
        activityRule.launchActivity(HotelDetailsActivity.createIntent(InstrumentationRegistry.getInstrumentation().targetContext, input))
    }

    private fun createInputIntent(hotelCode: String, roomTypes: List<String>): HotelDetailsInput {
        return HotelDetailsInput.builder()
                .cameFromMapView(false)
                .distanceFromSearchedLocation(0.0f)
                .hotelCode(hotelCode)
                .hotelImageUrl(null)
                .hotelName("London Blackfriars (Fleet Street)")
                .searchResultsInput(SearchResultsInput.builder()
                        .adults(listOf(1))
                        .infants(emptyList())
                        .children(listOf(1))
                        .infants(listOf(1))
                        .roomTypeCodes(roomTypes)
                        .cots(listOf(false))
                        .placeName("London Blackfriars (Fleet Street)")
                        .numRooms(1)
                        .arrivalDate(LocalDate.of(2019, 8, 23))
                        .departureDate(LocalDate.of(2019, 8, 24))
                        .latitude(51.513103f)
                        .longitude(-0.105613f)
                        .build()
                ).build()
    }
}