package uk.co.whitbread.integrationtests.stubs.opera

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import uk.co.whitbread.integrationtests.stubs.defaultStubsFor
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.Company
import uk.co.whitbread.integrationtests.testkit.model.CompanyAddress

/** Pins negotiated-rate request matching, present/empty responses, multiple mappings, and gate. */
class OperaNegotiatedRatesStubsTest :
    FunSpec({
        val enabled = company(companyId = "2569623", negotiatedRateEnabled = true)
        val disabled = company(companyId = "2569624", negotiatedRateEnabled = false)

        test("negotiated-rate mappings require the exact profile path and authenticated hub headers") {
            val request = companyNegotiatedRates(enabled).mappings.single().request

            request.method shouldBe "GET"
            request.urlPath shouldBe "/rtp/v1/profiles/2569623/negotiatedRates"
            request.queryParameters shouldBe null
            request.bodyPatterns shouldBe null
            val headers = request.headers.orEmpty()
            headers.getValue("Authorization").matches shouldBe "Bearer .+"
            headers.getValue("x-app-key").matches shouldBe ".+"
            headers.getValue("x-hubid").matches shouldBe ".+"
            headers.getValue("x-hotelid").absent shouldBe true
        }

        test("enabled and disabled companies drive present and empty rate collections") {
            val present = negotiatedRates(companyNegotiatedRates(enabled).mappings.single())
            val empty = negotiatedRates(companyNegotiatedRates(disabled).mappings.single())

            present.size shouldBe 1
            present
                .single()
                .jsonObject
                .getValue("hotelId")
                .jsonPrimitive.content shouldBe "DUBSOU"
            present
                .single()
                .jsonObject
                .getValue("ratePlanCode")
                .jsonPrimitive.content shouldBe "BUSIFLEX"
            empty.size shouldBe 0
        }

        test("one mapping is created for each company profile") {
            companyNegotiatedRates(listOf(enabled, disabled)).mappings.map { it.request.urlPath } shouldContainExactly
                listOf(
                    "/rtp/v1/profiles/2569623/negotiatedRates",
                    "/rtp/v1/profiles/2569624/negotiatedRates",
                )
        }

        test("the negotiated-rates default is gated by Booking companies") {
            val withCompany = Booking(companies = listOf(enabled))
            val withoutCompany = Booking()

            defaultStubsFor(withCompany).map { it.id }.contains(OPERA_COMPANY_NEGOTIATED_RATES_STUB_ID) shouldBe true
            defaultStubsFor(withoutCompany).map { it.id } shouldNotContain OPERA_COMPANY_NEGOTIATED_RATES_STUB_ID
        }
    })

private fun negotiatedRates(mapping: uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping) =
    mapping.response.jsonBody!!
        .jsonObject
        .getValue("negotiatedRates")
        .jsonArray

private fun company(
    companyId: String,
    negotiatedRateEnabled: Boolean,
): Company =
    Company(
        name = "Negotiated Rates Company $companyId",
        corpId = companyId,
        companyId = companyId,
        telephoneNumber = "+441234567890",
        negotiatedRateEnabled = negotiatedRateEnabled,
        address = CompanyAddress(addressLine1 = "1 Company Street", postalCode = "AB1 2CD"),
    )
