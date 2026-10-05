package uk.co.whitbread.integrationtests.stubs.opera

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import uk.co.whitbread.integrationtests.stubs.defaultStubsFor
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.Company
import uk.co.whitbread.integrationtests.testkit.model.CompanyAddress

/**
 * Pins the corporate-profile default's world state: the populated read under the corpId plus the
 * empty read under the raw companyId that drives a consumer's corporate-id-first fallback.
 */
class OperaCompanyByIdStubsTest :
    FunSpec({
        fun company(
            corpId: String = "1370",
            companyId: String = "2569623",
        ): Company =
            Company(
                name = "Gate Test Company",
                corpId = corpId,
                companyId = companyId,
                telephoneNumber = "+441234567890",
                address = CompanyAddress(addressLine1 = "1 Test Street", postalCode = "AB1 2CD"),
            )

        test("the default answers empty under the company id and populated under the corp id") {
            val subject = company()

            val mappings = companyProfiles(listOf(subject)).mappings

            mappings.size shouldBe 2
            val (empty, populated) = mappings
            empty.request.method shouldBe "GET"
            empty.request.urlPath shouldBe "/crm/v1/companies/${subject.companyId}"
            empty.response.jsonBody!!
                .jsonObject
                .shouldBeEmpty()
            populated.request.urlPath shouldBe "/crm/v1/companies/${subject.corpId}"
            populated.response.jsonBody!!
                .jsonObject
                .containsKey("companyDetails") shouldBe true
        }

        test("a company whose corp id equals its company id installs only the populated read") {
            val subject = company(corpId = "9000", companyId = "9000")

            val mappings = companyProfiles(listOf(subject)).mappings

            mappings.size shouldBe 1
            mappings.single().request.urlPath shouldBe "/crm/v1/companies/9000"
            mappings
                .single()
                .response.jsonBody!!
                .jsonObject
                .containsKey("companyDetails") shouldBe true
        }

        test("both corporate-profile mappings require exact instructions and authenticated hub headers") {
            val mappings = companyProfiles(listOf(company())).mappings

            mappings.forEach { mapping ->
                val fetchInstructions =
                    mapping.request.queryParameters
                        .orEmpty()
                        .getValue("fetchInstructions")
                        .hasExactly
                        .orEmpty()
                        .map { it.equalTo }
                fetchInstructions shouldBe listOf("ADDRESS", "COMMUNICATION", "SalesInfo", "Keyword", "Profile")

                val headers = mapping.request.headers.orEmpty()
                headers.getValue("Authorization").matches shouldBe "Bearer .+"
                headers.getValue("x-app-key").matches shouldBe ".+"
                headers.getValue("x-hubid").matches shouldBe ".+"
                headers.getValue("x-hotelid").absent shouldBe true
            }
        }

        test("the corporate-profile default is gated by Booking companies") {
            val withCompany = Booking(companies = listOf(company()))
            val withoutCompany = Booking()

            defaultStubsFor(withCompany).map { it.id }.contains(OPERA_COMPANY_PROFILE_STUB_ID) shouldBe true
            defaultStubsFor(withoutCompany).map { it.id } shouldNotContain OPERA_COMPANY_PROFILE_STUB_ID
        }

        test("the profile-by-company-id default is gated by Booking companies") {
            val withCompany = Booking(companies = listOf(company()))
            val withoutCompany = Booking()

            defaultStubsFor(withCompany).map { it.id }.contains(OPERA_COMPANY_PROFILE_BY_ID_STUB_ID) shouldBe true
            defaultStubsFor(withoutCompany).map { it.id } shouldNotContain OPERA_COMPANY_PROFILE_BY_ID_STUB_ID
        }

        test("the profile-by-company-id read pins one mapping per real caller shape") {
            val subject = company()

            val mappings = companyProfilesById(listOf(subject)).mappings
            mappings shouldHaveSize 2
            val (instructed, bare) = mappings

            // Reservation clients send the six repeated fetch instructions.
            instructed.request.method shouldBe "GET"
            instructed.request.urlPath shouldBe "/crm/v1/profiles/${subject.companyId}"
            instructed.request.queryParameters
                .orEmpty()
                .getValue("fetchInstructions")
                .hasExactly
                .orEmpty()
                .map { it.equalTo } shouldBe
                listOf("Profile", "Address", "Communication", "Correspondence", "FutureReservation", "HistoryReservation")

            // OhipProfileClient.getCompanyProfile (the company-id fallback) sends no query at all.
            bare.request.method shouldBe "GET"
            bare.request.urlPath shouldBe "/crm/v1/profiles/${subject.companyId}"
            bare.request.queryParameters
                .orEmpty()
                .getValue("fetchInstructions")
                .absent shouldBe true

            mappings.forEach { mapping ->
                val headers = mapping.request.headers.orEmpty()
                headers.getValue("Authorization").matches shouldBe "Bearer .+"
                headers.getValue("x-app-key").matches shouldBe ".+"
                headers.getValue("x-hubid").matches shouldBe ".+"
                headers.getValue("x-hotelid").absent shouldBe true
            }
        }

        test("the profile-by-company-id response keeps its ids and adds the company's address details") {
            val subject = company()

            val bodies =
                companyProfilesById(listOf(subject))
                    .mappings
                    .map { it.response.jsonBody!!.jsonObject }
                    .toSet()
            // Both caller-shape mappings serve the identical profile body.
            bodies shouldHaveSize 1
            val body = bodies.single()

            // The id list consumers already read stays exactly as it was.
            body.getValue("profileIdList").toString() shouldBe
                """[{"id":"2569623","type":"Profile"},{"id":"1370","type":"CorporateId"}]"""
            val addressInfo =
                body
                    .getValue("profileDetails")
                    .jsonObject
                    .getValue("addresses")
                    .jsonObject
                    .getValue("addressInfo")
                    .jsonArray
                    .single()
                    .jsonObject
            addressInfo.getValue("type").jsonPrimitive.content shouldBe "BUSINESS"
            addressInfo.getValue("primary").jsonPrimitive.content shouldBe "true"
            val address = addressInfo.getValue("address").jsonObject
            address
                .getValue("addressLine")
                .jsonArray
                .first()
                .jsonPrimitive
                .content shouldBe "1 Test Street"
            address.getValue("postalCode").jsonPrimitive.content shouldBe "AB1 2CD"
            address
                .getValue("country")
                .jsonObject
                .getValue("value")
                .jsonPrimitive
                .content shouldBe "GB"
        }

        test("an unlinked company's profile still omits the CorporateId entry") {
            val bodies =
                companyProfilesById(listOf(company().copy(corporateIdLinked = false)))
                    .mappings
                    .map { it.response.jsonBody!!.jsonObject }
                    .toSet()

            bodies shouldHaveSize 1
            bodies.single().getValue("profileIdList").toString() shouldBe """[{"id":"2569623","type":"Profile"}]"""
        }
    })
