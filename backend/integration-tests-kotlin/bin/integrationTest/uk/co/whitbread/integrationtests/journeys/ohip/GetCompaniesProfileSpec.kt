package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_COMPANY_PROFILE_SEARCH_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.companiesProfileSearchNotFound
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.Company
import uk.co.whitbread.integrationtests.testkit.presets.Companies
import uk.co.whitbread.integrationtests.testkit.presets.Hotels

private const val COMPANY_SEARCH_LIMIT = 10

// Both Opera token-service paths are environment-pinned OFF for this endpoint.
private val companiesProfileFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
        OhipFeatureFlag.USE_TOKEN_REFRESH_SKEW to false,
    )

/**
 * Proves GET /ohip/v1/profile/companies sends the exact encoded company-name search, gives AR
 * number precedence when both criteria are present, and maps an Opera rejection.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/GetCompaniesProfile.md
 */
class GetCompaniesProfileSpec :
    JourneySpec(
        "OHIP adapter searches Opera company profiles",
        {
            val ohipApi = OhipApi()

            scenario("a company-name search returns the matching Opera company profile") {
                val booking = companiesProfileBooking()
                val company = booking.companies.single()

                installFor(booking)

                val result =
                    ohipApi.getCompaniesProfile(
                        hotelId = booking.hotel.hotelId,
                        companyName = company.name,
                        limit = COMPANY_SEARCH_LIMIT,
                        testId = testId,
                        featureFlagOverrides = companiesProfileFlagPins,
                    )

                result.attachEvidence("Get Companies Profile By Name")

                expect("returns the matching company and paging identity") {
                    result.response.status.value shouldBe 200
                    result.body.totalResults shouldBe 1
                    result.body.limit shouldBe 1
                    result.body.companies
                        .single()
                        .name shouldBe company.name
                    result.body.companies
                        .single()
                        .companyId shouldBe company.companyId
                    result.body.companies
                        .single()
                        .corpId shouldBe company.corpId
                }

                expect("performs one exact company profile search") {
                    // One Opera call: GET /crm/v1/profiles by encoded profileName.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILES) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera rejection uses AR-number precedence over company name") {
                val booking = companiesProfileBooking(arNumber = "AR-9002")
                val company = booking.companies.single()

                installFor(booking, excluded = setOf(OPERA_COMPANY_PROFILE_SEARCH_STUB_ID))
                installStub(companiesProfileSearchNotFound(booking))

                val result =
                    ohipApi.getCompaniesProfile(
                        hotelId = booking.hotel.hotelId,
                        companyName = "Different Company Name",
                        arNumber = company.arNumber,
                        limit = COMPANY_SEARCH_LIMIT,
                        testId = testId,
                        featureFlagOverrides = companiesProfileFlagPins,
                    )

                result.attachEvidence("Get Companies Profile By AR Opera Error")

                expect("returns the mapped company-search error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 925
                }

                expect("attempts one rejected AR-number search") {
                    // One rejected Opera call: GET /crm/v1/profiles by aRNumber, without profileName.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILES) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun companiesProfileBooking(arNumber: String? = null): Booking {
    val company: Company =
        Companies.NEILL_TECHNICAL_SERVICES.copy(
            arNumber = arNumber,
        )
    return Booking(
        hotels = listOf(Hotels.HEAPTI),
        companies = listOf(company),
    )
}
