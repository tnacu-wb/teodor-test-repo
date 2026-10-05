package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.HotelRoomTypeResponse
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.HotelRoomType
import uk.co.whitbread.integrationtests.testkit.presets.Hotels

private val customRoomTypes =
    listOf(
        HotelRoomType(roomClass = "CT", roomType = "CODDBL", numberOfRooms = 3),
        HotelRoomType(roomClass = "CT", roomType = "CODACC", numberOfRooms = 1, accessible = true),
        HotelRoomType(roomClass = "SP", roomType = "CODFAM", numberOfRooms = 5),
    )

class GetHotelRoomTypesSpec :
    JourneySpec(
        "Hotel room types can be fetched from OHIP adapter service",
        {
            val ohipApi = OhipApi()

            scenario("GET /ohip/hotels/HEAPTI/roomTypes returns default room types") {
                val hotel = Hotels.HEAPTI
                val booking = Booking(hotels = listOf(hotel))

                installFor(booking)

                val result = ohipApi.getHotelRoomTypes(hotel.hotelId, testId)

                result.attachEvidence("Get Hotel Room Types HEAPTI")

                expect("returns mapped HEAPTI room types") {
                    result.response.status.value shouldBe 200
                    result.body.hotelId shouldBe hotel.hotelId
                    result.body.roomType shouldBe
                        hotel.availableRoomTypes.map {
                            HotelRoomTypeResponse(
                                roomClass = it.roomClass,
                                accessible = it.accessible,
                                roomType = it.roomType,
                                numberOfRooms = it.numberOfRooms.toString(),
                            )
                        }
                }
            }

            scenario("GET /ohip/hotels/FRAMTI/roomTypes returns default room types") {
                val hotel = Hotels.FRAMTI
                val booking = Booking(hotels = listOf(hotel))

                installFor(booking)

                val result = ohipApi.getHotelRoomTypes(hotel.hotelId, testId)

                result.attachEvidence("Get Hotel Room Types FRAMTI")

                expect("returns mapped FRAMTI room types") {
                    result.response.status.value shouldBe 200
                    result.body.hotelId shouldBe hotel.hotelId
                    result.body.roomType shouldBe
                        hotel.availableRoomTypes.map {
                            HotelRoomTypeResponse(
                                roomClass = it.roomClass,
                                accessible = it.accessible,
                                roomType = it.roomType,
                                numberOfRooms = it.numberOfRooms.toString(),
                            )
                        }
                }
            }

            scenario("GET /ohip/hotels/HEAPTI/roomTypes returns no room types") {
                val hotel = Hotels.HEAPTI.copy(availableRoomTypes = emptyList())
                val booking = Booking(hotels = listOf(hotel))

                installFor(booking)

                val result = ohipApi.getHotelRoomTypes(hotel.hotelId, testId)

                result.attachEvidence("Get Hotel Room Types Empty")

                expect("returns an empty room types list") {
                    result.response.status.value shouldBe 200
                    result.body.hotelId shouldBe hotel.hotelId
                    result.body.roomType shouldBe emptyList()
                }
            }

            scenario("GET /ohip/hotels/FRAMTI/roomTypes returns specified room types") {
                val hotel = Hotels.FRAMTI.copy(availableRoomTypes = customRoomTypes)
                val booking = Booking(hotels = listOf(hotel))

                installFor(booking)

                val result = ohipApi.getHotelRoomTypes(hotel.hotelId, testId)

                result.attachEvidence("Get Hotel Room Types Custom")

                expect("returns the specified room types") {
                    result.response.status.value shouldBe 200
                    result.body.hotelId shouldBe hotel.hotelId
                    result.body.roomType shouldBe
                        hotel.availableRoomTypes.map {
                            HotelRoomTypeResponse(
                                roomClass = it.roomClass,
                                accessible = it.accessible,
                                roomType = it.roomType,
                                numberOfRooms = it.numberOfRooms.toString(),
                            )
                        }
                }
            }
        },
    )
