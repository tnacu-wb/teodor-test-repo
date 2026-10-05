package uk.co.whitbread.integrationtests.stubs.aem

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import uk.co.whitbread.integrationtests.testkit.model.Hotel

private fun hotel(dataTransEnabled: Boolean) =
    Hotel(
        hotelId = "HEAPTI",
        shortId = "LONHEA",
        name = "London Heathrow Airport Terminal 4",
        addressLine = "Sheffield Road",
        city = "London",
        postcode = "TW6 3AF",
        phone = "+44 20 0000 0000",
        dataTransEnabled = dataTransEnabled,
    )

// The AEM builder serializes its merged template to a string body, so the assertions parse it
// back rather than reading a structured jsonBody the mapping does not carry.
private fun body(dataTransEnabled: Boolean) =
    Json
        .parseToJsonElement(
            hotelDetail(hotel(dataTransEnabled))
                .mappings
                .single()
                .response.body!!,
        ).jsonObject

private fun providerIds(dataTransEnabled: Boolean) =
    body(dataTransEnabled)
        .getValue("paymentProviders")
        .jsonArray
        .map {
            it.jsonObject
                .getValue("providerId")
                .jsonPrimitive.content
        }

class AemHotelDetailStubsTest :
    FunSpec({
        test("a hotel that does not take Datatrans reports only the 3CP provider") {
            body(dataTransEnabled = false)
                .getValue("isDataTransEnabled")
                .jsonPrimitive.content shouldBe "false"

            providerIds(dataTransEnabled = false) shouldBe listOf("3CP")
        }

        test("a Datatrans hotel keeps 3CP and adds Datatrans alongside it") {
            body(dataTransEnabled = true)
                .getValue("isDataTransEnabled")
                .jsonPrimitive.content shouldBe "true"

            // Both providers stay: payment-methods-entity-service reads whichever block matches
            // the provider it resolved, and other card options still resolve to 3CP.
            providerIds(dataTransEnabled = true) shouldBe listOf("3CP", "Datatrans")
        }

        test("the Datatrans provider carries Datatrans scheme codes, not the 3CP ones") {
            val datatransCodes =
                body(dataTransEnabled = true)
                    .getValue("paymentProviders")
                    .jsonArray
                    .single {
                        it.jsonObject
                            .getValue("providerId")
                            .jsonPrimitive.content == "Datatrans"
                    }.jsonObject
                    .getValue("paymentMethods")
                    .jsonArray
                    .map {
                        it.jsonObject
                            .getValue("code")
                            .jsonPrimitive.content
                    }

            // Datatrans publishes ECA/VIS/AMX/DIN/MAU where 3CP uses MC/VS/AX/DN/MA for the same
            // brands. Reusing the 3CP codes here would resolve but describe the wrong gateway.
            datatransCodes shouldBe listOf("ECA", "VIS", "AMX", "DIN", "MAU")
        }
    })
