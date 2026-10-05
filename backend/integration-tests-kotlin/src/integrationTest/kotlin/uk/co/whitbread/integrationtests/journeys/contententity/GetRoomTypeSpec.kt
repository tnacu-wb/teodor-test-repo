package uk.co.whitbread.integrationtests.journeys.contententity

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotContain
import uk.co.whitbread.integrationtests.clients.contententity.ContentEntityApi
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Aem
import uk.co.whitbread.integrationtests.testkit.model.AemRoomType
import uk.co.whitbread.integrationtests.testkit.model.AemRoomTypeInformation
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.presets.Hotels

private val roomTypeAem =
    AemRoomType(
        country = "gb",
        language = "en",
        brand = "pi",
        roomTypes =
            listOf(
                AemRoomTypeInformation(
                    roomTypeCode = "DOUBLE, ZPLDBL",
                    roomCategory = "Double",
                    roomLabel = "Double room",
                    roomDescription = "A super-comfy bed, a power shower and free Wi-Fi.",
                    gridImage = "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-2.jpg",
                    roomImage = "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-2.jpg",
                    groupId = "double",
                ),
                AemRoomTypeInformation(
                    roomTypeCode = "LOWDBL",
                    roomCategory = "Accessible room",
                    roomLabel = "Accessible double bedroom with a lowered bath",
                    roomDescription = "Accessible double bedroom with lowered bath from room-type data.",
                    gridImage = "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID4-Accessible-Double.jpg",
                    roomImage = "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID3-Accessible-Bedroom.jpg",
                    facilities = listOf("Free Wi-Fi"),
                    groupId = "accessible",
                    substitutionMessage = "<p>Accessible substitution message from AEM.</p>",
                ),
            ),
    )

private const val ACCESSIBLE_HOTEL_DETAIL_IMAGE =
    "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID4-Accessible-Double.jpg"

private const val ACCESSIBLE_HOTEL_DETAIL_DESCRIPTION =
    "Our accessible rooms offer more space, a double or kingsize bed between 480mm and 520mm in height, " +
        "and wider entry bathrooms with a lowered bath or wet room. "

private val unmatchedRoomTypeAem =
    roomTypeAem.copy(
        roomTypes =
            listOf(
                AemRoomTypeInformation(
                    roomTypeCode = "UNMATCHED",
                    roomCategory = "Standard",
                    roomLabel = "Unmatched test room",
                    roomDescription = "Description from room-type data for an unmatched code.",
                    gridImage = "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/unmatched-grid.jpg",
                    roomImage = "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/unmatched-room.jpg",
                    groupId = "double",
                ),
            ),
    )

class GetRoomTypeSpec :
    JourneySpec(
        "get room type information",
        {
            val contentEntityApi = ContentEntityApi()

            scenario("GET /v1/content/room-type returns room types from AEM") {
                val booking = Booking(aem = Aem(roomType = roomTypeAem))

                installFor(booking)

                val result =
                    contentEntityApi.getRoomType(
                        country = roomTypeAem.country,
                        language = roomTypeAem.language,
                        brand = "PI",
                        testId = testId,
                    )

                result.attachEvidence("Get Room Type")

                expect("returns mapped room types from AEM") {
                    result.response.status.value shouldBe 200
                    result.body.roomTypes shouldHaveSize roomTypeAem.roomTypes.size

                    val doubleRoom = result.body.roomTypes.first()
                    val doubleRoomAem = roomTypeAem.roomTypes.first()

                    doubleRoom.roomTypeCode shouldBe listOf("DOUBLE", "ZPLDBL")
                    doubleRoom.roomCategory shouldBe doubleRoomAem.roomCategory
                    doubleRoom.roomLabel shouldBe doubleRoomAem.roomLabel
                    doubleRoom.roomDescription shouldBe doubleRoomAem.roomDescription
                    doubleRoom.gridImage shouldBe doubleRoomAem.gridImage
                    doubleRoom.roomImage shouldBe doubleRoomAem.roomImage
                    doubleRoom.groupId shouldBe doubleRoomAem.groupId
                }

                expect("returns facilities and ignores AEM-only substitution message") {
                    val accessibleRoom = result.body.roomTypes[1]
                    val accessibleRoomAem = roomTypeAem.roomTypes[1]

                    accessibleRoom.roomTypeCode shouldBe listOf(accessibleRoomAem.roomTypeCode)
                    accessibleRoom.facilities shouldBe accessibleRoomAem.facilities
                    result.bodyText shouldNotContain "substitutionMessage"
                    result.bodyText shouldNotContain accessibleRoomAem.substitutionMessage
                }
            }

            scenario("GET /v1/content/room-type treats blank hotelId like no hotelId") {
                val booking = Booking(aem = Aem(roomType = roomTypeAem))

                installFor(booking)

                val result =
                    contentEntityApi.getRoomType(
                        country = roomTypeAem.country,
                        language = roomTypeAem.language,
                        brand = "PI",
                        hotelId = "",
                        testId = testId,
                    )

                result.attachEvidence("Get Room Type Blank Hotel Id")

                expect("returns room-type AEM values without hotel-detail enrichment") {
                    val accessibleRoom =
                        result.body.roomTypes
                            .single { it.roomTypeCode == listOf("LOWDBL") }
                    val accessibleRoomAem = roomTypeAem.roomTypes[1]

                    result.response.status.value shouldBe 200
                    accessibleRoom.roomImage shouldBe accessibleRoomAem.roomImage
                    accessibleRoom.roomDescription shouldBe accessibleRoomAem.roomDescription
                }
            }

            scenario("GET /v1/content/room-type enriches room image and description when hotelId is provided") {
                val hotel = Hotels.HEAPTI
                val booking =
                    Booking(
                        hotels = listOf(hotel),
                        aem = Aem(roomType = roomTypeAem),
                    )

                installFor(booking)

                val result =
                    contentEntityApi.getRoomType(
                        country = roomTypeAem.country,
                        language = roomTypeAem.language,
                        brand = "PI",
                        hotelId = hotel.hotelId,
                        testId = testId,
                    )

                result.attachEvidence("Get Room Type With Hotel Id")

                expect("overrides matching room image and description from hotel detail tab items") {
                    val accessibleRoom =
                        result.body.roomTypes
                            .single { it.roomTypeCode == listOf("LOWDBL") }

                    result.response.status.value shouldBe 200
                    accessibleRoom.roomImage shouldBe ACCESSIBLE_HOTEL_DETAIL_IMAGE
                    accessibleRoom.roomDescription shouldBe ACCESSIBLE_HOTEL_DETAIL_DESCRIPTION
                }

                expect("keeps room type data when the matching hotel tab item does not provide a rate-grid description") {
                    val doubleRoom =
                        result.body.roomTypes
                            .single { it.roomTypeCode == listOf("DOUBLE", "ZPLDBL") }
                    val doubleRoomAem = roomTypeAem.roomTypes.first()

                    doubleRoom.roomImage shouldBe
                        "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-room-no-sofa.jpg"
                    doubleRoom.roomDescription shouldBe doubleRoomAem.roomDescription
                }
            }

            scenario("GET /v1/content/room-type keeps AEM values when hotelId has no matching room code") {
                val hotel = Hotels.HEAPTI
                val booking =
                    Booking(
                        hotels = listOf(hotel),
                        aem = Aem(roomType = unmatchedRoomTypeAem),
                    )

                installFor(booking)

                val result =
                    contentEntityApi.getRoomType(
                        country = unmatchedRoomTypeAem.country,
                        language = unmatchedRoomTypeAem.language,
                        brand = "PI",
                        hotelId = hotel.hotelId,
                        testId = testId,
                    )

                result.attachEvidence("Get Room Type No Matching Hotel Room Code")

                expect("does not override room image or description") {
                    val unmatchedRoom = result.body.roomTypes.single()
                    val unmatchedRoomAem = unmatchedRoomTypeAem.roomTypes.single()

                    result.response.status.value shouldBe 200
                    unmatchedRoom.roomTypeCode shouldBe listOf(unmatchedRoomAem.roomTypeCode)
                    unmatchedRoom.roomImage shouldBe unmatchedRoomAem.roomImage
                    unmatchedRoom.roomDescription shouldBe unmatchedRoomAem.roomDescription
                }
            }

            scenario("GET /v1/content/room-type rejects missing required country before downstream calls") {
                // AEM is fully stubbed so a call would succeed. Zero recorded calls therefore
                // proves the request was rejected during validation, rather than proving only
                // that an unstubbed AEM happened to fail.
                val booking = Booking(aem = Aem(roomType = roomTypeAem))

                installFor(booking)

                val result =
                    contentEntityApi.getRoomType(
                        country = null,
                        language = roomTypeAem.language,
                        brand = "PI",
                        testId = testId,
                    )

                result.attachEvidence("Get Room Type Missing Country")

                expect("returns a validation error without calling AEM") {
                    result.response.status.value shouldBe 422
                    result.bodyText.isNotBlank() shouldBe true
                    callCount(Upstream.AEM) shouldBe 0
                }
            }

            scenario("GET /v1/content/room-type rejects missing required language before downstream calls") {
                val booking = Booking(aem = Aem(roomType = roomTypeAem))

                installFor(booking)

                val result =
                    contentEntityApi.getRoomType(
                        country = roomTypeAem.country,
                        language = null,
                        brand = "PI",
                        testId = testId,
                    )

                result.attachEvidence("Get Room Type Missing Language")

                expect("returns a validation error without calling AEM") {
                    result.response.status.value shouldBe 422
                    result.bodyText.isNotBlank() shouldBe true
                    callCount(Upstream.AEM) shouldBe 0
                }
            }

            scenario("GET /v1/content/room-type rejects missing required brand before downstream calls") {
                val booking = Booking(aem = Aem(roomType = roomTypeAem))

                installFor(booking)

                val result =
                    contentEntityApi.getRoomType(
                        country = roomTypeAem.country,
                        language = roomTypeAem.language,
                        brand = null,
                        testId = testId,
                    )

                result.attachEvidence("Get Room Type Missing Brand")

                expect("returns a validation error without calling AEM") {
                    result.response.status.value shouldBe 422
                    result.bodyText.isNotBlank() shouldBe true
                    callCount(Upstream.AEM) shouldBe 0
                }
            }
        },
    )
