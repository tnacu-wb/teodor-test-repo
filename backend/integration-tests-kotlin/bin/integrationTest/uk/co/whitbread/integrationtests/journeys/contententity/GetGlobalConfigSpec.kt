package uk.co.whitbread.integrationtests.journeys.contententity

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.contententity.ContentEntityApi
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Aem
import uk.co.whitbread.integrationtests.testkit.model.AemAllowedRoomTypesByOccupancy
import uk.co.whitbread.integrationtests.testkit.model.AemBookingWidgetConfig
import uk.co.whitbread.integrationtests.testkit.model.AemGlobalConfig
import uk.co.whitbread.integrationtests.testkit.model.AemGlobalConfigOffer
import uk.co.whitbread.integrationtests.testkit.model.AemRoomClassOrder
import uk.co.whitbread.integrationtests.testkit.model.AemRoomUpgrade
import uk.co.whitbread.integrationtests.testkit.model.AemRoomUpgradeOptions
import uk.co.whitbread.integrationtests.testkit.model.AemSite
import uk.co.whitbread.integrationtests.testkit.model.AemUpsellItemExtra
import uk.co.whitbread.integrationtests.testkit.model.Booking

private const val PREMIER_PLUS_IMAGE_URL =
    "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-1.jpg"

private val leisureGlobalConfig =
    AemGlobalConfig(
        country = "gb",
        language = "en",
        site = AemSite.LEISURE,
        brand = "pi",
        bookingWidgetConfig =
            AemBookingWidgetConfig(
                maxRooms = 9,
                maxRoomsAmend = 9,
                numberOfNights = 9,
                maxArrivalDate = 364,
                allowedRoomTypesByOccupancy =
                    listOf(
                        AemAllowedRoomTypesByOccupancy(
                            acceptedRoomTypes = listOf("FAM"),
                            adultsNumber = 2,
                            childrenNumber = 2,
                        ),
                        AemAllowedRoomTypesByOccupancy(
                            acceptedRoomTypes = listOf("DB", "TWIN", "DIS"),
                            adultsNumber = 2,
                            childrenNumber = 0,
                        ),
                    ),
            ),
        roomClassConfig =
            listOf(
                AemRoomClassOrder(
                    code = "PV",
                    order = 1,
                ),
                AemRoomClassOrder(
                    code = "ST",
                    order = 4,
                    availableUpgrades = listOf("SV", "PP", "PV"),
                ),
            ),
        roomUpgradeOptions =
            AemRoomUpgradeOptions(
                priceText = "Available for {{price}} per night",
                primaryButtonText = "Upgrade for {{price}}",
                secondaryButtonText = "Keep your booking",
                roomUpgrades =
                    listOf(
                        AemRoomUpgrade(
                            roomClass = "PP",
                            heading = "Upgrade to a Premier Plus room",
                            imageUrl = PREMIER_PLUS_IMAGE_URL,
                            description = "<p>Upgrade to one of our Premier Plus rooms.</p>",
                        ),
                    ),
            ),
        hotelsWithCityTax = listOf("EDIPRI", "MANPMI"),
        upsellItemsExtras =
            listOf(
                AemUpsellItemExtra(
                    promoText = "Free Ultimate Wi-Fi - 24 hours",
                    packageCode = "FI24HR",
                    promoPackageCode = "FIFR24",
                ),
            ),
    )

private val employeeGlobalConfig =
    leisureGlobalConfig.copy(
        offers =
            listOf(
                AemGlobalConfigOffer(
                    page = "employee-offer",
                    maxRooms = 2,
                    maxRoomsAmend = 2,
                    numberOfNights = 5,
                    maxArrivalDate = 90,
                    allowedRoomTypesByOccupancy =
                        listOf(
                            AemAllowedRoomTypesByOccupancy(
                                acceptedRoomTypes = listOf("SB", "DB", "DIS"),
                                adultsNumber = 1,
                                childrenNumber = 0,
                            ),
                        ),
                ),
            ),
    )

private val travelIndustryGlobalConfig =
    leisureGlobalConfig.copy(
        offers =
            listOf(
                AemGlobalConfigOffer(
                    page = "travel-industry-rate",
                    maxRooms = 3,
                    maxRoomsAmend = 3,
                    numberOfNights = 7,
                    maxArrivalDate = 120,
                    allowedRoomTypesByOccupancy =
                        listOf(
                            AemAllowedRoomTypesByOccupancy(
                                acceptedRoomTypes = listOf("DB", "TWIN"),
                                adultsNumber = 2,
                                childrenNumber = 0,
                            ),
                        ),
                ),
            ),
    )

private val businessBookerGlobalConfig =
    leisureGlobalConfig.copy(
        site = AemSite.BUSINESS_BOOKER,
    )

private val ccuiGlobalConfig =
    leisureGlobalConfig.copy(
        site = AemSite.CCUI,
    )

private val distributionGlobalConfig =
    leisureGlobalConfig.copy(
        site = AemSite.DISTRIBUTION,
    )

class GetGlobalConfigSpec :
    JourneySpec(
        "get global config",
        {
            val contentEntityApi = ContentEntityApi()

            scenario("GET /v1/content/global-config returns leisure global config") {
                val booking = Booking(aem = Aem(globalConfig = leisureGlobalConfig))

                installFor(booking)

                val result =
                    contentEntityApi.getGlobalConfig(
                        channelId = "PI",
                        country = leisureGlobalConfig.country,
                        language = leisureGlobalConfig.language,
                        brand = "PI",
                        testId = testId,
                    )

                result.attachEvidence("Get Global Config")

                expect("returns booking widget limits from AEM global config") {
                    val maxRoomsLim = result.body.maxRoomsLim.shouldNotBeNull()

                    result.response.status.value shouldBe 200
                    maxRoomsLim.maxRooms shouldBe leisureGlobalConfig.bookingWidgetConfig.maxRooms
                    maxRoomsLim.maxRoomsAmend shouldBe leisureGlobalConfig.bookingWidgetConfig.maxRoomsAmend
                    maxRoomsLim.maxNights shouldBe leisureGlobalConfig.bookingWidgetConfig.numberOfNights
                    maxRoomsLim.maxArrivalDate shouldBe leisureGlobalConfig.bookingWidgetConfig.maxArrivalDate
                    maxRoomsLim.roomOccupancies shouldHaveSize
                        leisureGlobalConfig.bookingWidgetConfig.allowedRoomTypesByOccupancy.size
                    maxRoomsLim.roomOccupancies.first().acceptedRoomTypes shouldBe listOf("FAM")
                }

                expect("returns room classes from AEM global config") {
                    result.body.roomClassConfig shouldHaveSize leisureGlobalConfig.roomClassConfig.size
                    result.body.roomClassConfig
                        .first()
                        .code shouldBe "PV"
                    result.body.roomClassConfig
                        .first()
                        .order shouldBe 1
                    result.body.roomClassConfig
                        .last()
                        .availableUpgrades shouldBe listOf("SV", "PP", "PV")
                }

                expect("returns upgrade options from AEM global config") {
                    val roomUpgradeOptions = result.body.roomUpgradeOptions.shouldNotBeNull()

                    roomUpgradeOptions.priceText shouldBe leisureGlobalConfig.roomUpgradeOptions?.priceText
                    roomUpgradeOptions.primaryButtonText shouldBe leisureGlobalConfig.roomUpgradeOptions?.primaryButtonText
                    roomUpgradeOptions.secondaryButtonText shouldBe
                        leisureGlobalConfig.roomUpgradeOptions?.secondaryButtonText
                    roomUpgradeOptions.roomUpgrades shouldHaveSize 1
                    roomUpgradeOptions.roomUpgrades.first().roomClass shouldBe "PP"
                    roomUpgradeOptions.roomUpgrades.first().heading shouldBe "Upgrade to a Premier Plus room"
                }

                expect("returns pass-through global config lists from AEM") {
                    result.body.hotelsWithCityTax shouldBe leisureGlobalConfig.hotelsWithCityTax
                    result.body.upsellItemsExtras shouldHaveSize 1
                    result.body.upsellItemsExtras
                        .first()
                        .promoText shouldBe
                        leisureGlobalConfig.upsellItemsExtras.first().promoText
                    result.body.upsellItemsExtras
                        .first()
                        .packageCode shouldBe
                        leisureGlobalConfig.upsellItemsExtras.first().packageCode
                    result.body.upsellItemsExtras
                        .first()
                        .promoPackageCode shouldBe
                        leisureGlobalConfig.upsellItemsExtras.first().promoPackageCode
                }
            }

            scenario("GET /v1/content/global-config applies default country language and brand") {
                val booking = Booking(aem = Aem(globalConfig = leisureGlobalConfig))

                installFor(booking)

                val result =
                    contentEntityApi.getGlobalConfig(
                        channelId = "PI",
                        country = null,
                        language = null,
                        brand = null,
                        testId = testId,
                    )

                result.attachEvidence("Get Global Config Defaults")

                expect("returns global config from the default AEM path") {
                    val maxRoomsLim = result.body.maxRoomsLim.shouldNotBeNull()

                    result.response.status.value shouldBe 200
                    maxRoomsLim.maxRooms shouldBe leisureGlobalConfig.bookingWidgetConfig.maxRooms
                    result.body.roomClassConfig
                        .first()
                        .code shouldBe "PV"
                }
            }

            scenario("GET /v1/content/global-config applies employee offer search rules") {
                val booking = Booking(aem = Aem(globalConfig = employeeGlobalConfig))

                installFor(booking)

                val result =
                    contentEntityApi.getGlobalConfig(
                        channelId = "EMPLOYEE",
                        country = employeeGlobalConfig.country,
                        language = employeeGlobalConfig.language,
                        brand = "PI",
                        testId = testId,
                    )

                result.attachEvidence("Get Global Config Employee")

                expect("returns max rooms limits from the employee offer") {
                    val maxRoomsLim = result.body.maxRoomsLim.shouldNotBeNull()
                    val employeeOffer = employeeGlobalConfig.offers.single()

                    result.response.status.value shouldBe 200
                    maxRoomsLim.maxRooms shouldBe employeeOffer.maxRooms
                    maxRoomsLim.maxRoomsAmend shouldBe employeeOffer.maxRoomsAmend
                    maxRoomsLim.maxNights shouldBe employeeOffer.numberOfNights
                    maxRoomsLim.maxArrivalDate shouldBe employeeOffer.maxArrivalDate
                    maxRoomsLim.roomOccupancies shouldHaveSize 1
                    maxRoomsLim.roomOccupancies.first().acceptedRoomTypes shouldBe listOf("SB", "DB", "DIS")
                }
            }

            scenario("GET /v1/content/global-config applies travel industry offer search rules") {
                val booking = Booking(aem = Aem(globalConfig = travelIndustryGlobalConfig))

                installFor(booking)

                val result =
                    contentEntityApi.getGlobalConfig(
                        channelId = "FCDNLR30",
                        country = travelIndustryGlobalConfig.country,
                        language = travelIndustryGlobalConfig.language,
                        brand = "PI",
                        testId = testId,
                    )

                result.attachEvidence("Get Global Config Travel Industry")

                expect("returns max rooms limits from the travel industry offer") {
                    val maxRoomsLim = result.body.maxRoomsLim.shouldNotBeNull()
                    val travelIndustryOffer = travelIndustryGlobalConfig.offers.single()

                    result.response.status.value shouldBe 200
                    maxRoomsLim.maxRooms shouldBe travelIndustryOffer.maxRooms
                    maxRoomsLim.maxRoomsAmend shouldBe travelIndustryOffer.maxRoomsAmend
                    maxRoomsLim.maxNights shouldBe travelIndustryOffer.numberOfNights
                    maxRoomsLim.maxArrivalDate shouldBe travelIndustryOffer.maxArrivalDate
                    maxRoomsLim.roomOccupancies shouldHaveSize 1
                    maxRoomsLim.roomOccupancies.first().acceptedRoomTypes shouldBe listOf("DB", "TWIN")
                }
            }

            scenario("GET /v1/content/global-config routes business booker channel to business-booker AEM path") {
                val booking = Booking(aem = Aem(globalConfig = businessBookerGlobalConfig))

                installFor(booking)

                val result =
                    contentEntityApi.getGlobalConfig(
                        channelId = "BB",
                        country = businessBookerGlobalConfig.country,
                        language = businessBookerGlobalConfig.language,
                        brand = "PI",
                        testId = testId,
                    )

                result.attachEvidence("Get Global Config Business Booker")

                expect("returns global config from the business-booker AEM path") {
                    val maxRoomsLim = result.body.maxRoomsLim.shouldNotBeNull()

                    result.response.status.value shouldBe 200
                    maxRoomsLim.maxRooms shouldBe businessBookerGlobalConfig.bookingWidgetConfig.maxRooms
                    result.body.roomClassConfig
                        .first()
                        .code shouldBe "PV"
                }
            }

            scenario("GET /v1/content/global-config routes CCUI channel to ccui AEM path") {
                val booking = Booking(aem = Aem(globalConfig = ccuiGlobalConfig))

                installFor(booking)

                val result =
                    contentEntityApi.getGlobalConfig(
                        channelId = "CCUI",
                        country = ccuiGlobalConfig.country,
                        language = ccuiGlobalConfig.language,
                        brand = "PI",
                        testId = testId,
                    )

                result.attachEvidence("Get Global Config CCUI")

                expect("returns global config from the ccui AEM path") {
                    val maxRoomsLim = result.body.maxRoomsLim.shouldNotBeNull()

                    result.response.status.value shouldBe 200
                    maxRoomsLim.maxRooms shouldBe ccuiGlobalConfig.bookingWidgetConfig.maxRooms
                    result.body.roomClassConfig
                        .first()
                        .code shouldBe "PV"
                }
            }

            scenario("GET /v1/content/global-config routes distribution channel to distribution AEM path") {
                val booking = Booking(aem = Aem(globalConfig = distributionGlobalConfig))

                installFor(booking)

                val result =
                    contentEntityApi.getGlobalConfig(
                        channelId = "DISTR",
                        country = distributionGlobalConfig.country,
                        language = distributionGlobalConfig.language,
                        brand = "PI",
                        testId = testId,
                    )

                result.attachEvidence("Get Global Config Distribution")

                expect("returns global config from the distribution AEM path") {
                    val maxRoomsLim = result.body.maxRoomsLim.shouldNotBeNull()

                    result.response.status.value shouldBe 200
                    maxRoomsLim.maxRooms shouldBe distributionGlobalConfig.bookingWidgetConfig.maxRooms
                    result.body.roomClassConfig
                        .first()
                        .code shouldBe "PV"
                }
            }

            scenario("GET /v1/content/global-config normalises mixed case request values") {
                val booking = Booking(aem = Aem(globalConfig = businessBookerGlobalConfig))

                installFor(booking)

                val result =
                    contentEntityApi.getGlobalConfig(
                        channelId = "bB",
                        country = "Gb",
                        language = "En",
                        brand = "Pi",
                        testId = testId,
                    )

                result.attachEvidence("Get Global Config Mixed Case")

                expect("returns global config from the normalised business-booker AEM path") {
                    val maxRoomsLim = result.body.maxRoomsLim.shouldNotBeNull()

                    result.response.status.value shouldBe 200
                    maxRoomsLim.maxRooms shouldBe businessBookerGlobalConfig.bookingWidgetConfig.maxRooms
                    result.body.roomClassConfig
                        .first()
                        .code shouldBe "PV"
                }
            }

            scenario("GET /v1/content/global-config rejects missing channelId before downstream calls") {
                // AEM is fully stubbed so a call would succeed. Zero recorded calls therefore
                // proves the request was rejected during validation, rather than proving only
                // that an unstubbed AEM happened to fail.
                val booking = Booking(aem = Aem(globalConfig = leisureGlobalConfig))

                installFor(booking)

                val result =
                    contentEntityApi.getGlobalConfig(
                        channelId = null,
                        country = "gb",
                        language = "en",
                        brand = "PI",
                        testId = testId,
                    )

                result.attachEvidence("Get Global Config Missing ChannelId")

                expect("returns a validation error without calling AEM") {
                    result.response.status.value shouldBe 422
                    result.bodyText.isNotBlank() shouldBe true
                    callCount(Upstream.AEM) shouldBe 0
                }
            }
        },
    )
