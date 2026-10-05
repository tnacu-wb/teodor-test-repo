package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.HotelPackageCodesRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.HotelPackageGroup
import uk.co.whitbread.integrationtests.clients.ohip.model.HotelPackageGroupCode
import uk.co.whitbread.integrationtests.clients.ohip.model.HotelPackageGroupsRequest
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.PackageCatalogue
import uk.co.whitbread.integrationtests.testkit.model.PackageComponent
import uk.co.whitbread.integrationtests.testkit.model.PackageComposition
import uk.co.whitbread.integrationtests.testkit.model.PackageDefinition
import uk.co.whitbread.integrationtests.testkit.presets.Hotels

class GetHotelPackageGroupsSpec :
    JourneySpec(
        "Hotel package groups can be fetched from OHIP adapter service",
        {
            val ohipApi = OhipApi()

            scenario("POST /ohip/hotels/packages/groups returns an explicitly requested package group") {
                val hotel = Hotels.HEAPTI
                val packageGroup = hotel.composablePackage("MDP")

                installFor(packageGroupBooking(hotel))

                val result =
                    ohipApi.getHotelPackageGroups(
                        request =
                            HotelPackageGroupsRequest(
                                hotelId = hotel.hotelId,
                                packageGroupList = listOf(packageGroup.code),
                            ),
                        testId = testId,
                    )

                result.attachEvidence("Get Hotel Package Groups Explicit Group")

                expect("returns the requested package group members") {
                    result.response.status.value shouldBe 200
                    result.body.packagesGroup shouldBe listOf(packageGroup.toExpectedGroup())
                }
            }

            scenario("POST /ohip/hotels/packages/groups returns no groups for a normal package code") {
                val hotel = Hotels.HEAPTI
                val normalPackage =
                    hotel.packageCatalogue
                        .shouldNotBeNull()
                        .packages
                        .first { it.composition == null }

                installFor(packageGroupBooking(hotel))

                val result =
                    ohipApi.getHotelPackageGroups(
                        request =
                            HotelPackageGroupsRequest(
                                hotelId = hotel.hotelId,
                                packageGroupList = listOf(normalPackage.code),
                            ),
                        testId = testId,
                    )

                result.attachEvidence("Get Hotel Package Groups Normal Package")

                expect("does not treat a normal package as a package group") {
                    result.response.status.value shouldBe 200
                    result.body.packagesGroup shouldBe emptyList()
                }
            }

            scenario("POST /ohip/hotels/packages/groups ignores blank package group values") {
                val hotel = Hotels.HEAPTI
                val packageGroup = hotel.composablePackage("MDP")

                installFor(packageGroupBooking(hotel))

                val result =
                    ohipApi.getHotelPackageGroups(
                        request =
                            HotelPackageGroupsRequest(
                                hotelId = hotel.hotelId,
                                packageGroupList = listOf("", " ", packageGroup.code),
                            ),
                        testId = testId,
                    )

                result.attachEvidence("Get Hotel Package Groups Blank Groups")

                expect("uses the cleaned package group list") {
                    result.response.status.value shouldBe 200
                    result.body.packagesGroup shouldBe listOf(packageGroup.toExpectedGroup())
                }
            }

            scenario("POST /ohip/hotels/packages/groups finds a group by exact package-code members") {
                val hotel = Hotels.HEAPTI
                val packageGroup = hotel.composablePackage("MDP")
                val memberCodes =
                    packageGroup.composition
                        .shouldNotBeNull()
                        .members
                        .map { it.code }

                installFor(packageGroupBooking(hotel))

                val result =
                    ohipApi.getHotelPackageGroups(
                        request =
                            HotelPackageGroupsRequest(
                                hotelId = hotel.hotelId,
                                packageCodeList = listOf(HotelPackageCodesRequest(packageCodes = memberCodes)),
                            ),
                        testId = testId,
                    )

                result.attachEvidence("Get Hotel Package Groups By Member Codes")

                expect("returns the group whose members match the requested package-code set") {
                    result.response.status.value shouldBe 200
                    result.body.packagesGroup shouldBe listOf(packageGroup.toExpectedGroup())
                }
            }

            scenario("POST /ohip/hotels/packages/groups returns all groups when no group or code filter is supplied") {
                val hotel = Hotels.FRAMTI
                val expectedGroups =
                    hotel.packageCatalogue
                        .shouldNotBeNull()
                        .packages
                        .filter { it.composition != null }
                        .map { it.toExpectedGroup() }

                installFor(packageGroupBooking(hotel))

                val result =
                    ohipApi.getHotelPackageGroups(
                        request = HotelPackageGroupsRequest(hotelId = hotel.hotelId),
                        testId = testId,
                    )

                result.attachEvidence("Get Hotel Package Groups All Groups")

                expect("returns all package groups with members") {
                    result.response.status.value shouldBe 200
                    result.body.packagesGroup shouldBe expectedGroups
                }
            }

            scenario("POST /ohip/hotels/packages/groups returns custom package composition data") {
                val customPackage =
                    PackageDefinition(
                        code = "BUNDLE1",
                        description = "Custom Bundle",
                        price = 18.5,
                        currency = "GBP",
                        composition =
                            PackageComposition(
                                description = "Custom Bundle Package",
                                members =
                                    listOf(
                                        PackageComponent("CUSTFOOD", "Custom Bundle Food"),
                                        PackageComponent("CUSTDRNK", "Custom Bundle Drink"),
                                    ),
                            ),
                    )
                val hotel =
                    Hotels.HEAPTI.copy(
                        packageCatalogue = PackageCatalogue(packages = listOf(customPackage)),
                    )

                installFor(packageGroupBooking(hotel))

                val result =
                    ohipApi.getHotelPackageGroups(
                        request =
                            HotelPackageGroupsRequest(
                                hotelId = hotel.hotelId,
                                packageGroupList = listOf(customPackage.code),
                            ),
                        testId = testId,
                    )

                result.attachEvidence("Get Hotel Package Groups Custom")

                expect("returns only the custom package group") {
                    result.response.status.value shouldBe 200
                    result.body.packagesGroup shouldBe listOf(customPackage.toExpectedGroup())
                }
            }
        },
    )

private fun packageGroupBooking(hotel: Hotel): Booking = Booking(hotels = listOf(hotel))

private fun Hotel.composablePackage(code: String): PackageDefinition =
    packageCatalogue
        .shouldNotBeNull()
        .packages
        .first { it.code == code }
        .also { it.composition.shouldNotBeNull() }

private fun PackageDefinition.toExpectedGroup(): HotelPackageGroup {
    val composition = composition.shouldNotBeNull()
    return HotelPackageGroup(
        packageGroup = code,
        packageGroupDescription = composition.description ?: description,
        packageCodes =
            composition.members.map { member ->
                HotelPackageGroupCode(
                    packageCode = member.code,
                    packageDescription = member.description,
                )
            },
    )
}
