package uk.co.whitbread.integrationtests.journeys.contententity

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import uk.co.whitbread.integrationtests.clients.contententity.ContentEntityApi
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_DONATION_PACKAGES_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_HOTEL_CONFIG_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_PACKAGE_GROUP_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_ROOM_TYPES_STUB_ID
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.model.Aem
import uk.co.whitbread.integrationtests.testkit.model.AemHreflang
import uk.co.whitbread.integrationtests.testkit.model.AemIndexHeaderData
import uk.co.whitbread.integrationtests.testkit.model.AemIndexHeaderFavicon
import uk.co.whitbread.integrationtests.testkit.model.AemIndexHeaderIcon
import uk.co.whitbread.integrationtests.testkit.model.AemIndexHeaderMsIcon
import uk.co.whitbread.integrationtests.testkit.model.AemIndexHeaderSeo
import uk.co.whitbread.integrationtests.testkit.model.AemSearchResultsData
import uk.co.whitbread.integrationtests.testkit.model.AemSearchResultsSeo
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.presets.Hotels

private val homeIndexHeaderData =
    AemIndexHeaderData(
        country = "gb",
        language = "en",
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
                            href = "/content/dam/pi/websites/desktop/icons/favicons/xfavicon-228x228.png.pagespeed.ic.AhL0MMwPlZ.webp",
                        ),
                    ),
                msIcons =
                    listOf(
                        AemIndexHeaderMsIcon(
                            name = "msapplication-TileImage",
                            content = "/content/dam/pi/websites/desktop/icons/favicons/favicon-ie10-144x144.png",
                        ),
                    ),
            ),
    )

private val srpSearchResultsData =
    AemSearchResultsData(
        country = "gb",
        language = "en",
        seo =
            AemSearchResultsSeo(
                pageTitle = "Search results | Premier Inn",
                pageDescription = "Search results page description",
                cardImageUrl = "/content/dam/pi/websites/facebook-twitter/search-results-ogimage.jpg",
                hreflangs =
                    listOf(
                        AemHreflang(
                            hreflang = "x-default",
                            href = "https://www.premierinn.com/gb/en/search.html",
                        ),
                        AemHreflang(
                            hreflang = "en-gb",
                            href = "https://www.premierinn.com/gb/en/search.html",
                        ),
                        AemHreflang(
                            hreflang = "de-de",
                            href = "https://www.premierinn.com/de/de/search.html",
                        ),
                    ),
            ),
    )

class GetSeoSpec :
    JourneySpec(
        "get seo information",
        {
            val contentEntityApi = ContentEntityApi()

            scenario("GET /v1/content/seo returns Home SEO metadata") {
                val booking = Booking(aem = Aem(indexHeaderData = homeIndexHeaderData))

                installFor(booking)

                val result =
                    contentEntityApi.getSeo(
                        page = "Home",
                        country = homeIndexHeaderData.country,
                        language = homeIndexHeaderData.language,
                        testId = testId,
                    )

                result.attachEvidence("Get SEO Home")

                expect("returns SEO metadata from index header data") {
                    result.response.status.value shouldBe 200
                    result.body.pageTitle shouldBe homeIndexHeaderData.seo.pageTitle
                    result.body.pageDescription shouldBe homeIndexHeaderData.seo.pageDescription
                    result.body.cardImageUrl shouldBe homeIndexHeaderData.seo.cardImageUrl
                    result.body.faviconUrl shouldBe homeIndexHeaderData.favicon.faviconUrl
                }

                expect("returns favicon icon metadata from index header data") {
                    result.body.icons shouldHaveSize 1
                    result.body.icons
                        .first()
                        .rel shouldBe
                        homeIndexHeaderData.favicon.icons
                            .first()
                            .rel
                    result.body.icons
                        .first()
                        .sizes shouldBe
                        homeIndexHeaderData.favicon.icons
                            .first()
                            .sizes
                    result.body.icons
                        .first()
                        .href shouldBe
                        homeIndexHeaderData.favicon.icons
                            .first()
                            .href

                    result.body.msIcons shouldHaveSize 1
                    result.body.msIcons
                        .first()
                        .name shouldBe
                        homeIndexHeaderData.favicon.msIcons
                            .first()
                            .name
                    result.body.msIcons
                        .first()
                        .content shouldBe
                        homeIndexHeaderData.favicon.msIcons
                            .first()
                            .content
                }
            }

            scenario("GET /v1/content/seo returns HDP SEO metadata") {
                val hotel = Hotels.HEAPTI
                val booking =
                    Booking(
                        hotels = listOf(hotel),
                        aem = Aem(indexHeaderData = homeIndexHeaderData),
                    )

                installFor(
                    booking,
                    excluded =
                        setOf(
                            OPERA_HOTEL_CONFIG_STUB_ID,
                            OPERA_ROOM_TYPES_STUB_ID,
                            OPERA_PACKAGE_GROUP_STUB_ID,
                            OPERA_DONATION_PACKAGES_STUB_ID,
                        ),
                )

                val result =
                    contentEntityApi.getSeo(
                        page = "HDP",
                        hotelId = hotel.hotelId,
                        country = homeIndexHeaderData.country,
                        language = homeIndexHeaderData.language,
                        testId = testId,
                    )

                result.attachEvidence("Get SEO HDP")

                expect("returns hotel SEO metadata from hotel detail data") {
                    result.response.status.value shouldBe 200
                    result.body.pageTitle shouldBe "Premier Inn ${hotel.name} hotel"
                    result.body.pageDescription.shouldNotBeNull() shouldContain hotel.name
                }

                expect("returns shared SEO assets from index header data") {
                    result.body.cardImageUrl shouldBe homeIndexHeaderData.seo.cardImageUrl
                    result.body.faviconUrl shouldBe homeIndexHeaderData.favicon.faviconUrl
                    result.body.icons shouldHaveSize homeIndexHeaderData.favicon.icons.size
                    result.body.msIcons shouldHaveSize homeIndexHeaderData.favicon.msIcons.size
                }

                expect("returns hotel hreflangs from hotel detail data") {
                    val hreflangs = result.body.hreflangs.shouldNotBeNull()

                    hreflangs shouldHaveSize 3
                    hreflangs.first().hreflang shouldBe "x-default"
                    hreflangs.first().href.shouldNotBeNull() shouldContain "london-heathrow-airport-m4j4.html"
                }
            }

            scenario("GET /v1/content/seo returns bad request when HDP hotelId is missing") {
                val result =
                    contentEntityApi.getSeo(
                        page = "HDP",
                        country = homeIndexHeaderData.country,
                        language = homeIndexHeaderData.language,
                        testId = testId,
                    )

                result.attachEvidence("Get SEO HDP Missing Hotel Id")

                expect("returns the missing hotelId validation error") {
                    val error = result.errorBody.shouldNotBeNull()

                    result.response.status.value shouldBe 400
                    error.errCode shouldBe 6
                    error.debugMessage shouldBe "hotelId mandatory for this page"
                    error.globalErrTextTemplate shouldBe "validation.error.form"
                }
            }

            scenario("GET /v1/content/seo returns bad request when booking-flow hotelId is missing") {
                val booking = Booking(aem = Aem(indexHeaderData = homeIndexHeaderData))

                installFor(booking)

                val result =
                    contentEntityApi.getSeo(
                        page = "Confirmation",
                        bookingFlowId = "booking-a1",
                        country = homeIndexHeaderData.country,
                        language = homeIndexHeaderData.language,
                        testId = testId,
                    )

                result.attachEvidence("Get SEO Confirmation Missing Hotel Id")

                expect("returns the missing booking-flow hotelId validation error") {
                    val error = result.errorBody.shouldNotBeNull()

                    result.response.status.value shouldBe 400
                    error.errCode shouldBe 7
                    error.debugMessage shouldBe "hotelId mandatory for this page"
                    error.globalErrTextTemplate shouldBe "validation.error.form"
                }
            }

            scenario("GET /v1/content/seo returns bad request when bookingFlowId is missing") {
                val booking = Booking(aem = Aem(indexHeaderData = homeIndexHeaderData))

                installFor(booking)

                val result =
                    contentEntityApi.getSeo(
                        page = "Confirmation",
                        hotelId = Hotels.HEAPTI.hotelId,
                        country = homeIndexHeaderData.country,
                        language = homeIndexHeaderData.language,
                        testId = testId,
                    )

                result.attachEvidence("Get SEO Confirmation Missing Booking Flow Id")

                expect("returns the missing bookingFlowId validation error") {
                    val error = result.errorBody.shouldNotBeNull()

                    result.response.status.value shouldBe 400
                    error.errCode shouldBe 8
                    error.debugMessage shouldBe "bookingFlowId mandatory for this page"
                    error.globalErrTextTemplate shouldBe "validation.error.form"
                }
            }

            scenario("GET /v1/content/seo returns SRP SEO metadata") {
                val booking =
                    Booking(
                        aem =
                            Aem(
                                indexHeaderData = homeIndexHeaderData,
                                searchResultsData = srpSearchResultsData,
                            ),
                    )

                installFor(booking)

                val result =
                    contentEntityApi.getSeo(
                        page = "SRP",
                        country = srpSearchResultsData.country,
                        language = srpSearchResultsData.language,
                        testId = testId,
                    )

                result.attachEvidence("Get SEO SRP")

                expect("returns SRP SEO metadata from search results data") {
                    result.response.status.value shouldBe 200
                    result.body.pageTitle shouldBe srpSearchResultsData.seo.pageTitle
                    result.body.pageDescription shouldBe srpSearchResultsData.seo.pageDescription
                    result.body.cardImageUrl shouldBe srpSearchResultsData.seo.cardImageUrl
                }

                expect("returns shared favicon metadata from index header data") {
                    result.body.faviconUrl shouldBe homeIndexHeaderData.favicon.faviconUrl
                    result.body.icons shouldHaveSize homeIndexHeaderData.favicon.icons.size
                    result.body.msIcons shouldHaveSize homeIndexHeaderData.favicon.msIcons.size
                }

                expect("returns hreflangs from search results data") {
                    val hreflangs = result.body.hreflangs.shouldNotBeNull()

                    hreflangs shouldHaveSize srpSearchResultsData.seo.hreflangs.size
                    hreflangs.first().hreflang shouldBe
                        srpSearchResultsData.seo.hreflangs
                            .first()
                            .hreflang
                    hreflangs.first().href shouldBe
                        srpSearchResultsData.seo.hreflangs
                            .first()
                            .href
                }
            }
        },
    )
