package uk.co.whitbread.integrationtests.journeys.pages

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import uk.co.whitbread.integrationtests.clients.contententity.ContentEntityApi
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.model.Aem
import uk.co.whitbread.integrationtests.testkit.model.AemAllowedRoomTypesByOccupancy
import uk.co.whitbread.integrationtests.testkit.model.AemBookingWidgetConfig
import uk.co.whitbread.integrationtests.testkit.model.AemCookieDurationConfig
import uk.co.whitbread.integrationtests.testkit.model.AemCookieGroup
import uk.co.whitbread.integrationtests.testkit.model.AemCookieIntroView
import uk.co.whitbread.integrationtests.testkit.model.AemCookieManageView
import uk.co.whitbread.integrationtests.testkit.model.AemCookiePolicies
import uk.co.whitbread.integrationtests.testkit.model.AemGlobalConfig
import uk.co.whitbread.integrationtests.testkit.model.AemIndexHeaderData
import uk.co.whitbread.integrationtests.testkit.model.AemIndexHeaderFavicon
import uk.co.whitbread.integrationtests.testkit.model.AemIndexHeaderIcon
import uk.co.whitbread.integrationtests.testkit.model.AemIndexHeaderMsIcon
import uk.co.whitbread.integrationtests.testkit.model.AemIndexHeaderSeo
import uk.co.whitbread.integrationtests.testkit.model.AemRoomClassOrder
import uk.co.whitbread.integrationtests.testkit.model.AemRoomType
import uk.co.whitbread.integrationtests.testkit.model.AemRoomTypeInformation
import uk.co.whitbread.integrationtests.testkit.model.AemRoomUpgrade
import uk.co.whitbread.integrationtests.testkit.model.AemRoomUpgradeOptions
import uk.co.whitbread.integrationtests.testkit.model.AemSite
import uk.co.whitbread.integrationtests.testkit.model.AemUpsellItemExtra
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.hotelPagePath
import uk.co.whitbread.integrationtests.testkit.presets.Hotels

private const val COUNTRY = "gb"
private const val LANGUAGE = "en"
private const val BRAND = "pi"

class HotelDetailsPageSpec :
    JourneySpec(
        "hotel details page",
        {
            val contentEntityApi = ContentEntityApi()

            scenario("Hotel Details Page loads currently covered backend content") {
                val hotel = Hotels.HEAPTI
                val booking =
                    Booking(
                        hotels = listOf(hotel),
                        aem =
                            Aem(
                                indexHeaderData =
                                    AemIndexHeaderData(
                                        country = COUNTRY,
                                        language = LANGUAGE,
                                        seo =
                                            AemIndexHeaderSeo(
                                                pageTitle = "Premier Inn hotels | Book direct",
                                                pageDescription = "From booking to bed, we are here to help you rest easy.",
                                                cardImageUrl = "/content/dam/pi/websites/facebook-twitter/premierinn-ogimage.jpg",
                                            ),
                                        favicon =
                                            AemIndexHeaderFavicon(
                                                faviconUrl = "/content/dam/pi/websites/desktop/icons/favicons/favicon.ico",
                                                icons =
                                                    listOf(
                                                        AemIndexHeaderIcon(
                                                            rel = "icon",
                                                            sizes = "228x228",
                                                            href =
                                                                "/content/dam/pi/websites/desktop/icons/favicons/" +
                                                                    "xfavicon-228x228.png.pagespeed.ic.AhL0MMwPlZ.webp",
                                                        ),
                                                    ),
                                                msIcons =
                                                    listOf(
                                                        AemIndexHeaderMsIcon(
                                                            name = "msapplication-TileImage",
                                                            content =
                                                                "/content/dam/pi/websites/desktop/icons/favicons/" +
                                                                    "favicon-ie10-144x144.png",
                                                        ),
                                                    ),
                                            ),
                                    ),
                                globalConfig =
                                    AemGlobalConfig(
                                        country = COUNTRY,
                                        language = LANGUAGE,
                                        site = AemSite.LEISURE,
                                        brand = BRAND,
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
                                                    ),
                                            ),
                                        roomClassConfig =
                                            listOf(
                                                AemRoomClassOrder(
                                                    code = "ST",
                                                    order = 4,
                                                    availableUpgrades = listOf("PP"),
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
                                                            imageUrl =
                                                                "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/" +
                                                                    "PremierPlus/Premier-Plus-Room-1.jpg",
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
                                    ),
                                roomType =
                                    AemRoomType(
                                        country = COUNTRY,
                                        language = LANGUAGE,
                                        brand = BRAND,
                                        roomTypes =
                                            listOf(
                                                AemRoomTypeInformation(
                                                    roomTypeCode = "DOUBLE, ZPLDBL",
                                                    roomCategory = "Double",
                                                    roomLabel = "Double room",
                                                    roomDescription = "A super-comfy bed, a power shower and free Wi-Fi.",
                                                    gridImage =
                                                        "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/" +
                                                            "ID4/ID4-Room-2.jpg",
                                                    roomImage =
                                                        "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/" +
                                                            "ID4/ID4-Room-2.jpg",
                                                    groupId = "double",
                                                ),
                                                AemRoomTypeInformation(
                                                    roomTypeCode = "LOWDBL",
                                                    roomCategory = "Accessible room",
                                                    roomLabel = "Accessible double bedroom with a lowered bath",
                                                    roomDescription = "Accessible double bedroom with lowered bath from room-type data.",
                                                    gridImage =
                                                        "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/" +
                                                            "Accessible/ID4-Accessible-Double.jpg",
                                                    roomImage =
                                                        "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/" +
                                                            "Accessible/ID3-Accessible-Bedroom.jpg",
                                                    groupId = "accessible",
                                                ),
                                            ),
                                    ),
                                cookiePolicies =
                                    AemCookiePolicies(
                                        country = COUNTRY,
                                        language = LANGUAGE,
                                        brand = BRAND,
                                        version = "hdp-shell",
                                        config =
                                            AemCookieDurationConfig(
                                                cookieOptOutExpiryDays = "14",
                                                cookieOptInExpiryDays = "180",
                                            ),
                                        introView =
                                            AemCookieIntroView(
                                                title = "HDP cookie preferences",
                                                description = "Short page-test cookie intro from AEM.",
                                                manageButtonText = "Choose cookies",
                                                acceptAllButtonText = "Accept all",
                                                necessaryOnlyButtonText = "Essential only",
                                            ),
                                        manageView =
                                            AemCookieManageView(
                                                title = "Cookie settings",
                                                description = "<p>Page-test cookie settings copy.</p>\n",
                                                saveSettingsButtonText = "Save choices",
                                                alwaysActiveText = "Always active",
                                                cookieGroup =
                                                    listOf(
                                                        AemCookieGroup(
                                                            cookieName = "hdpEssential",
                                                            title = "HDP essential",
                                                            description = "<p>Required page-shell cookies.</p>\n",
                                                            isAlwaysActive = true,
                                                            toggleLabel = "HDP essential cookies are always active.",
                                                        ),
                                                        AemCookieGroup(
                                                            cookieName = "hdpInsights",
                                                            title = "HDP insights",
                                                            description = "<p>Optional page-shell analytics cookies.</p>\n",
                                                            isAlwaysActive = false,
                                                            toggleLabel = "Choose whether HDP insights cookies are active.",
                                                        ),
                                                    ),
                                            ),
                                    ),
                            ),
                    )
                val expectedAem = booking.aem.shouldNotBeNull()
                val expectedIndexHeaderData = expectedAem.indexHeaderData.shouldNotBeNull()
                val expectedGlobalConfig = expectedAem.globalConfig.shouldNotBeNull()
                val expectedRoomType = expectedAem.roomType.shouldNotBeNull()
                val expectedCookiePolicies = expectedAem.cookiePolicies.shouldNotBeNull()

                installFor(booking)

                val hotelInformation =
                    contentEntityApi.getHotelBySlug(
                        slug = hotelPagePath(hotel),
                        country = COUNTRY,
                        language = LANGUAGE,
                        testId = testId,
                    )
                val hotelInformationById =
                    contentEntityApi.getHotelInformation(
                        hotelId = hotel.hotelId,
                        country = COUNTRY,
                        language = LANGUAGE,
                        testId = testId,
                    )
                val seoInformation =
                    contentEntityApi.getSeo(
                        page = "HDP",
                        hotelId = hotel.hotelId,
                        country = COUNTRY,
                        language = LANGUAGE,
                        testId = testId,
                    )
                val headerInformation =
                    contentEntityApi.getIndexHeaderData(
                        country = COUNTRY,
                        language = LANGUAGE,
                        testId = testId,
                    )
                val globalConfig =
                    contentEntityApi.getGlobalConfig(
                        channelId = "PI",
                        country = COUNTRY,
                        language = LANGUAGE,
                        brand = "PI",
                        testId = testId,
                    )
                val roomTypeInformation =
                    contentEntityApi.getRoomType(
                        country = COUNTRY,
                        language = LANGUAGE,
                        brand = "PI",
                        hotelId = hotel.hotelId,
                        testId = testId,
                    )
                val cookiePolicies =
                    contentEntityApi.getCookiePolicies(
                        country = COUNTRY,
                        language = LANGUAGE,
                        brand = BRAND,
                        testId = testId,
                    )

                hotelInformation.attachEvidence("HDP Hotel Information")
                hotelInformationById.attachEvidence("HDP Hotel Information By Id")
                seoInformation.attachEvidence("HDP SEO Information")
                headerInformation.attachEvidence("HDP Header Information")
                globalConfig.attachEvidence("HDP Global Config")
                roomTypeInformation.attachEvidence("HDP Room Type Information")
                cookiePolicies.attachEvidence("HDP Cookie Policies")

                expect("loads hotel information for the page slug") {
                    hotelInformation.response.status.value shouldBe 200
                    hotelInformation.body.hotelId shouldBe hotel.hotelId
                    hotelInformation.body.name shouldBe hotel.name
                    hotelInformation.body.title shouldBe "${hotel.name} hotel"
                    hotelInformation.body.links
                        .shouldNotBeNull()
                        .detailsPage shouldBe
                        hotelPagePath(hotel).removePrefix("/hotels").removeSuffix(".html")
                }

                expect("loads hotel information for the selected hotel id") {
                    hotelInformationById.response.status.value shouldBe 200
                    hotelInformationById.body.hotelId shouldBe hotel.hotelId
                    hotelInformationById.body.name shouldBe hotel.name
                    hotelInformationById.body.title shouldBe "${hotel.name} hotel"
                    hotelInformationById.body.links
                        .shouldNotBeNull()
                        .detailsPage shouldBe
                        hotelPagePath(hotel).removePrefix("/hotels").removeSuffix(".html")
                }

                expect("loads HDP SEO information for the selected hotel") {
                    val hreflangs = seoInformation.body.hreflangs.shouldNotBeNull()

                    seoInformation.response.status.value shouldBe 200
                    seoInformation.body.pageTitle shouldBe "Premier Inn ${hotel.name} hotel"
                    seoInformation.body.pageDescription.shouldNotBeNull() shouldContain hotel.name
                    seoInformation.body.faviconUrl shouldBe expectedIndexHeaderData.favicon.faviconUrl
                    hreflangs shouldHaveSize 3
                    hreflangs.first().href.shouldNotBeNull() shouldContain hotelPagePath(hotel)
                }

                expect("loads header information needed by the page shell") {
                    val content = headerInformation.body.content.shouldNotBeNull()
                    val seo = content.seo.shouldNotBeNull()
                    val favicon = content.favicon.shouldNotBeNull()

                    headerInformation.response.status.value shouldBe 200
                    seo.pageTitle shouldBe expectedIndexHeaderData.seo.pageTitle
                    seo.pageDescription shouldBe expectedIndexHeaderData.seo.pageDescription
                    seo.cardImageUrl shouldBe expectedIndexHeaderData.seo.cardImageUrl
                    favicon.faviconUrl shouldBe expectedIndexHeaderData.favicon.faviconUrl
                    favicon.icons shouldHaveSize expectedIndexHeaderData.favicon.icons.size
                    favicon.msIcons shouldHaveSize expectedIndexHeaderData.favicon.msIcons.size
                }

                expect("loads global config needed by the page shell") {
                    val maxRoomsLim = globalConfig.body.maxRoomsLim.shouldNotBeNull()

                    globalConfig.response.status.value shouldBe 200
                    maxRoomsLim.maxRooms shouldBe expectedGlobalConfig.bookingWidgetConfig.maxRooms
                    maxRoomsLim.maxRoomsAmend shouldBe expectedGlobalConfig.bookingWidgetConfig.maxRoomsAmend
                    maxRoomsLim.maxNights shouldBe expectedGlobalConfig.bookingWidgetConfig.numberOfNights
                    globalConfig.body.roomClassConfig
                        .single()
                        .code shouldBe "ST"
                    globalConfig.body.roomUpgradeOptions
                        .shouldNotBeNull()
                        .roomUpgrades
                        .single()
                        .roomClass shouldBe "PP"
                }

                expect("loads room type information for the selected hotel") {
                    roomTypeInformation.response.status.value shouldBe 200
                    roomTypeInformation.body.roomTypes shouldHaveSize expectedRoomType.roomTypes.size
                    roomTypeInformation.body.roomTypes
                        .first()
                        .roomTypeCode shouldBe listOf("DOUBLE", "ZPLDBL")
                    roomTypeInformation.body.roomTypes
                        .last()
                        .roomTypeCode shouldBe listOf("LOWDBL")
                }

                expect("loads cookie policies needed by the page shell") {
                    val cookiePoliciesBody = cookiePolicies.body.cookiePolicies.shouldNotBeNull()
                    val config = cookiePoliciesBody.config.shouldNotBeNull()
                    val manageView = cookiePoliciesBody.manageView.shouldNotBeNull()

                    cookiePolicies.response.status.value shouldBe 200
                    cookiePoliciesBody.version shouldBe expectedCookiePolicies.version
                    cookiePoliciesBody.brand shouldBe expectedCookiePolicies.brand
                    config.cookieOptOutExpiryDays shouldBe expectedCookiePolicies.config.cookieOptOutExpiryDays
                    config.cookieOptInExpiryDays shouldBe expectedCookiePolicies.config.cookieOptInExpiryDays
                    manageView.cookieGroup shouldHaveSize expectedCookiePolicies.manageView.cookieGroup.size
                }
            }
        },
    )
