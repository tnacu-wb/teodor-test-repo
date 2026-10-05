package uk.co.whitbread.integrationtests.stubs.opera

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import uk.co.whitbread.integrationtests.stubs.defaultStubsFor
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.Company
import uk.co.whitbread.integrationtests.testkit.model.CompanyAddress
import uk.co.whitbread.integrationtests.testkit.model.Hotel

/** Pins the exact name/AR company-summary searches, Booking-driven response, and default gate. */
class OperaCompaniesProfileSearchStubsTest :
    FunSpec({
        val hotel =
            Hotel(
                hotelId = "DUBSOU",
                shortId = "DSO",
                name = "Search Hotel",
                addressLine = "1 Search Street",
                city = "Dublin",
                postcode = "D01 TEST",
                phone = "+3531000000",
            )
        val companyWithoutAr = company(name = "Named Company", corpId = "1370", companyId = "2569623")
        val companyWithAr =
            company(
                name = "AR Company",
                corpId = "1371",
                companyId = "2569624",
                arNumber = "AR-123",
            )
        val booking = Booking(hotels = listOf(hotel), companies = listOf(companyWithoutAr, companyWithAr))

        test("name and AR mappings require mutually exclusive exact query shapes") {
            val mappings = companiesProfileSearch(booking).mappings

            mappings.size shouldBe 3
            val name =
                mappings.single { mapping ->
                    mapping.request.queryParameters
                        .orEmpty()["profileName"]
                        ?.equalTo == "%25Named Company"
                }
            val nameQuery = name.request.queryParameters.orEmpty()
            name.request.method shouldBe "GET"
            name.request.urlPath shouldBe "/crm/v1/profiles"
            name.request.bodyPatterns shouldBe null
            nameQuery.getValue("profileName").equalTo shouldBe "%25Named Company"
            nameQuery.getValue("aRNumber").absent shouldBe true
            assertFixedSearchQuery(nameQuery)

            val ar =
                mappings.single { mapping ->
                    mapping.request.queryParameters
                        .orEmpty()["aRNumber"]
                        ?.equalTo == "AR-123"
                }
            val arQuery = ar.request.queryParameters.orEmpty()
            arQuery.getValue("aRNumber").equalTo shouldBe "AR-123"
            arQuery.getValue("profileName").absent shouldBe true
            assertFixedSearchQuery(arQuery)
        }

        test("company-summary searches require authenticated hotel headers and no hub header") {
            companiesProfileSearch(booking).mappings.forEach { mapping ->
                val headers = mapping.request.headers.orEmpty()

                headers.getValue("Authorization").matches shouldBe "Bearer .+"
                headers.getValue("x-app-key").matches shouldBe ".+"
                headers.getValue("x-hotelid").equalTo shouldBe "DUBSOU"
                headers.getValue("x-hubid").absent shouldBe true
            }
        }

        test("each search response is driven by its matching company") {
            val mapping =
                companiesProfileSearch(booking).mappings.single { candidate ->
                    candidate.request.queryParameters
                        .orEmpty()["profileName"]
                        ?.equalTo == "%25Named Company"
                }
            val summaries =
                mapping.response.jsonBody!!
                    .jsonObject
                    .getValue("profileSummaries")
                    .jsonObject
            val profileInfo =
                summaries
                    .getValue("profileInfo")
                    .jsonArray
                    .single()
                    .jsonObject

            summaries.getValue("totalResults").jsonPrimitive.content shouldBe "1"
            summaries.getValue("limit").jsonPrimitive.content shouldBe "1"
            profileInfo
                .getValue("profile")
                .jsonObject
                .getValue("formerName")
                .jsonObject
                .getValue("fullName")
                .jsonPrimitive
                .content shouldBe companyWithoutAr.name
        }

        test("the default gate requires both a hotel and a company") {
            val withoutCompany = booking.copy(companies = emptyList())
            val withoutHotel = booking.copy(hotels = emptyList())

            defaultStubsFor(booking).map { it.id }.contains(OPERA_COMPANY_PROFILE_SEARCH_STUB_ID) shouldBe true
            defaultStubsFor(withoutCompany).map { it.id } shouldNotContain OPERA_COMPANY_PROFILE_SEARCH_STUB_ID
            defaultStubsFor(withoutHotel).map { it.id } shouldNotContain OPERA_COMPANY_PROFILE_SEARCH_STUB_ID
        }
    })

private fun assertFixedSearchQuery(query: Map<String, uk.co.whitbread.integrationtests.framework.wiremock.model.StringValuePattern>) {
    query.getValue("profileType").equalTo shouldBe "Company"
    query.getValue("includePurgeProfiles").equalTo shouldBe "false"
    query.getValue("accountsReceivables").equalTo shouldBe "true"
    query.getValue("excludeInactive").equalTo shouldBe "true"
    query.getValue("includeAnonymized").equalTo shouldBe "true"
    query.getValue("fetchInstructions").equalTo shouldBe "SalesInfo"
    query.getValue("limit").matches shouldBe "[1-9][0-9]*"
}

private fun company(
    name: String,
    corpId: String,
    companyId: String,
    arNumber: String? = null,
): Company =
    Company(
        name = name,
        corpId = corpId,
        companyId = companyId,
        telephoneNumber = "+441234567890",
        arNumber = arNumber,
        address = CompanyAddress(addressLine1 = "1 Company Street", postalCode = "AB1 2CD"),
    )
