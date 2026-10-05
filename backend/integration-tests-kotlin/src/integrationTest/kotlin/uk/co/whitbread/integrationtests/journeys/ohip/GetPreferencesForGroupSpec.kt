package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_HOTEL_PREFERENCES_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.hotelPreferencesFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.HotelPreference
import uk.co.whitbread.integrationtests.testkit.model.HotelPreferenceGroup
import uk.co.whitbread.integrationtests.testkit.presets.Hotels

private val preferencesFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
    )

/**
 * Proves the OHIP adapter's hotel preference lookup: `GET /ohip/v1/preference/hotels/{hotelId}`
 * passes the requested preference group code to Opera CRM config and maps the returned
 * preference rows straight through.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/GetPreferencesForGroup.md
 */
class GetPreferencesForGroupSpec :
    JourneySpec(
        "OHIP adapter returns hotel preferences for a group",
        {
            val ohipApi = OhipApi()

            scenario("a preference group's rows are returned for the hotel") {
                val group =
                    HotelPreferenceGroup(
                        groupCode = "PILLOW",
                        preferences =
                            listOf(
                                HotelPreference(code = "FIRM", description = "Firm pillow", orderSequence = 1),
                                HotelPreference(code = "SOFT", description = "Soft pillow", orderSequence = 2),
                            ),
                    )
                val booking = Booking(hotels = listOf(Hotels.HEAPTI.copy(preferenceGroups = listOf(group))))

                installFor(booking)

                val result =
                    ohipApi.getPreferencesForGroup(
                        hotelId = booking.hotel.hotelId,
                        preferenceGroupsCodes = group.groupCode,
                        testId = testId,
                        featureFlagOverrides = preferencesFlagPins,
                    )

                result.attachEvidence("Get Preferences For Group")

                expect("returns the group's preference rows") {
                    result.response.status.value shouldBe 200
                    val rows = result.body.hotelPreferences.orEmpty()
                    rows.size shouldBe 2
                    rows.map { it.code } shouldBe listOf("FIRM", "SOFT")
                    rows.first().preferenceGroup shouldBe group.groupCode
                    rows.first().description shouldBe "Firm pillow"
                }

                expect("reads only the Opera preferences") {
                    // One Opera call: GET preferences with the group code.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_PREFERENCES) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a rejected Opera preferences read maps to the hotel-config error") {
                val group =
                    HotelPreferenceGroup(
                        groupCode = "PILLOW",
                        preferences = listOf(HotelPreference(code = "FIRM", description = "Firm pillow")),
                    )
                val booking = Booking(hotels = listOf(Hotels.HEAPTI.copy(preferenceGroups = listOf(group))))

                installFor(booking, excluded = setOf(OPERA_HOTEL_PREFERENCES_STUB_ID))
                installStub(hotelPreferencesFailure(booking.hotel))

                val result =
                    ohipApi.getPreferencesForGroup(
                        hotelId = booking.hotel.hotelId,
                        preferenceGroupsCodes = group.groupCode,
                        testId = testId,
                        featureFlagOverrides = preferencesFlagPins,
                    )

                result.attachEvidence("Get Preferences Opera Error")

                expect("returns the mapped hotel-config error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 962
                }

                expect("stops after the rejected preferences read") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_PREFERENCES) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )
