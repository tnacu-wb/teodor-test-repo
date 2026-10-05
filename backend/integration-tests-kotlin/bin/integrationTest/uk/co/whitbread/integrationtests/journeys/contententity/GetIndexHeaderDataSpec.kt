package uk.co.whitbread.integrationtests.journeys.contententity

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.contententity.ContentEntityApi
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Aem
import uk.co.whitbread.integrationtests.testkit.model.AemIndexHeaderAnnouncement
import uk.co.whitbread.integrationtests.testkit.model.AemIndexHeaderData
import uk.co.whitbread.integrationtests.testkit.model.AemIndexHeaderFavicon
import uk.co.whitbread.integrationtests.testkit.model.AemIndexHeaderFeatures
import uk.co.whitbread.integrationtests.testkit.model.AemIndexHeaderIcon
import uk.co.whitbread.integrationtests.testkit.model.AemIndexHeaderMsIcon
import uk.co.whitbread.integrationtests.testkit.model.AemIndexHeaderSeo
import uk.co.whitbread.integrationtests.testkit.model.AemSite
import uk.co.whitbread.integrationtests.testkit.model.Booking

private val leisureIndexHeaderData =
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
                            href = "/content/dam/pi/websites/desktop/icons/favicons/favicon-228x228.png",
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

private val businessBookerIndexHeaderData =
    leisureIndexHeaderData.copy(
        site = AemSite.BUSINESS_BOOKER,
        seo =
            leisureIndexHeaderData.seo.copy(
                pageTitle = "Business Booker | Premier Inn",
                pageDescription = "Manage business bookings with Premier Inn.",
            ),
    )

private val announcementDisabledIndexHeaderData =
    leisureIndexHeaderData.copy(
        announcement =
            AemIndexHeaderAnnouncement(
                text = "Planned maintenance tonight",
                type = "warning",
                browserCompatibilityMessage = "Please update your browser.",
            ),
        features =
            AemIndexHeaderFeatures(
                announcement = false,
            ),
    )

class GetIndexHeaderDataSpec :
    JourneySpec(
        "get index header data",
        {
            val contentEntityApi = ContentEntityApi()

            scenario("GET /v1/content/header/data returns standard index header data") {
                val booking = Booking(aem = Aem(indexHeaderData = leisureIndexHeaderData))

                installFor(booking)

                val result =
                    contentEntityApi.getIndexHeaderData(
                        country = leisureIndexHeaderData.country,
                        language = leisureIndexHeaderData.language,
                        testId = testId,
                    )

                result.attachEvidence("Get Index Header Data")

                expect("returns SEO metadata from AEM index header data") {
                    val content = result.body.content.shouldNotBeNull()
                    val seo = content.seo.shouldNotBeNull()

                    result.response.status.value shouldBe 200
                    seo.pageTitle shouldBe leisureIndexHeaderData.seo.pageTitle
                    seo.pageDescription shouldBe leisureIndexHeaderData.seo.pageDescription
                    seo.cardImageUrl shouldBe leisureIndexHeaderData.seo.cardImageUrl
                }

                expect("returns favicon metadata from AEM index header data") {
                    val favicon =
                        result.body.content
                            .shouldNotBeNull()
                            .favicon
                            .shouldNotBeNull()

                    favicon.faviconUrl shouldBe leisureIndexHeaderData.favicon.faviconUrl
                    favicon.icons shouldHaveSize 1
                    favicon.icons.first().rel shouldBe
                        leisureIndexHeaderData.favicon.icons
                            .first()
                            .rel
                    favicon.icons.first().sizes shouldBe
                        leisureIndexHeaderData.favicon.icons
                            .first()
                            .sizes
                    favicon.icons.first().href shouldBe
                        leisureIndexHeaderData.favicon.icons
                            .first()
                            .href
                    favicon.msIcons shouldHaveSize 1
                    favicon.msIcons.first().name shouldBe
                        leisureIndexHeaderData.favicon.msIcons
                            .first()
                            .name
                    favicon.msIcons.first().content shouldBe
                        leisureIndexHeaderData.favicon.msIcons
                            .first()
                            .content
                }
            }

            scenario("GET /v1/content/header/data routes businessBooker=true to business-booker AEM path") {
                val booking = Booking(aem = Aem(indexHeaderData = businessBookerIndexHeaderData))

                installFor(booking)

                val result =
                    contentEntityApi.getIndexHeaderData(
                        country = businessBookerIndexHeaderData.country,
                        language = businessBookerIndexHeaderData.language,
                        businessBooker = true,
                        testId = testId,
                    )

                result.attachEvidence("Get Index Header Data Business Booker")

                expect("returns business-booker index header data") {
                    val seo =
                        result.body.content
                            .shouldNotBeNull()
                            .seo
                            .shouldNotBeNull()

                    result.response.status.value shouldBe 200
                    seo.pageTitle shouldBe businessBookerIndexHeaderData.seo.pageTitle
                    seo.pageDescription shouldBe businessBookerIndexHeaderData.seo.pageDescription
                }
            }

            scenario("GET /v1/content/header/data clears announcement text when the feature is disabled") {
                val booking = Booking(aem = Aem(indexHeaderData = announcementDisabledIndexHeaderData))

                installFor(booking)

                val result =
                    contentEntityApi.getIndexHeaderData(
                        country = announcementDisabledIndexHeaderData.country,
                        language = announcementDisabledIndexHeaderData.language,
                        testId = testId,
                    )

                result.attachEvidence("Get Index Header Data Announcement Disabled")

                expect("preserves the announcement object but clears text") {
                    val announcement = result.body.announcement.shouldNotBeNull()
                    val expectedAnnouncement = announcementDisabledIndexHeaderData.announcement.shouldNotBeNull()

                    result.response.status.value shouldBe 200
                    announcement.text shouldBe null
                    announcement.type shouldBe expectedAnnouncement.type
                    announcement.browserCompatibilityMessage shouldBe expectedAnnouncement.browserCompatibilityMessage
                }
            }

            scenario("GET /v1/content/header/data rejects missing required country before downstream calls") {
                // AEM is fully stubbed so a call would succeed. Zero recorded calls therefore
                // proves the request was rejected during validation, rather than proving only
                // that an unstubbed AEM happened to fail.
                val booking = Booking(aem = Aem(indexHeaderData = leisureIndexHeaderData))

                installFor(booking)

                val result =
                    contentEntityApi.getIndexHeaderData(
                        country = null,
                        language = leisureIndexHeaderData.language,
                        testId = testId,
                    )

                result.attachEvidence("Get Index Header Data Missing Country")

                expect("returns a validation error without calling AEM") {
                    result.response.status.value shouldBe 422
                    result.bodyText.isNotBlank() shouldBe true
                    callCount(Upstream.AEM) shouldBe 0
                }
            }

            scenario("GET /v1/content/header/data rejects missing required language before downstream calls") {
                val booking = Booking(aem = Aem(indexHeaderData = leisureIndexHeaderData))

                installFor(booking)

                val result =
                    contentEntityApi.getIndexHeaderData(
                        country = leisureIndexHeaderData.country,
                        language = null,
                        testId = testId,
                    )

                result.attachEvidence("Get Index Header Data Missing Language")

                expect("returns a validation error without calling AEM") {
                    result.response.status.value shouldBe 422
                    result.bodyText.isNotBlank() shouldBe true
                    callCount(Upstream.AEM) shouldBe 0
                }
            }
        },
    )
