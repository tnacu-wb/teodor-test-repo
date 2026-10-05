package uk.co.whitbread.integrationtests.journeys.contententity

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.contententity.ContentEntityApi
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_HOTEL_CONFIG_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_ROOM_TYPES_STUB_ID
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.model.Aem
import uk.co.whitbread.integrationtests.testkit.model.AemFooter
import uk.co.whitbread.integrationtests.testkit.model.AemSite
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.presets.Hotels

private val footer =
    AemFooter(
        country = "gb",
        language = "en",
        site = AemSite.BUSINESS_BOOKER,
    )

class GetFooterBusinessBookerEnGbSpec :
    JourneySpec(
        "get footer business booker en gb",
        {
            scenario("GET /v1/content/footer for business-booker gb/en") {
                val booking =
                    Booking(
                        hotels = listOf(Hotels.HEAPTI),
                        aem = Aem(footer = footer),
                    )

                installFor(
                    booking,
                    excluded =
                        setOf(
                            OPERA_HOTEL_CONFIG_STUB_ID,
                            OPERA_ROOM_TYPES_STUB_ID,
                        ),
                )

                val footerFixture = booking.aem?.footer.shouldNotBeNull()
                val result = ContentEntityApi().getFooter(footerFixture, testId)

                result.attachEvidence("Get Footer Business Booker")

                expect("returns footer content") {
                    result.response.status.value shouldBe 200
                    result.body.tabs shouldHaveSize 1
                    result.body.copyrightInfo shouldBe "&copy; 2026 Premier Inn"
                }

                val tab = result.body.tabs.first()

                expect("maps AEM tab and intro") {
                    tab.name shouldBe "Business Booker"
                    tab.intro.shouldNotBeNull().name shouldBe ""
                    tab.intro.shouldNotBeNull().description shouldBe ""
                    tab.columns shouldHaveSize 6
                }

                expect("maps About us links") {
                    val aboutUs = tab.columns[0]
                    aboutUs.name shouldBe "About us"
                    aboutUs.linkItems shouldHaveSize 5
                    aboutUs.linkItems[0].name shouldBe "Why we're Premier"
                    aboutUs.linkItems[0].linkSrc shouldBe
                        "https://www.dit.premierinn.digital/gb/en/business-booker/why.html?intcmp=bbfooter.html"
                    aboutUs.linkItems[0].openInNewTab shouldBe false
                    aboutUs.linkItems[2].name shouldBe "Premier Inn CleanProtect\u2122"
                }

                expect("maps Business guests links") {
                    val businessGuests = tab.columns[1]
                    businessGuests.name shouldBe "Business guests"
                    businessGuests.linkItems shouldHaveSize 5
                    businessGuests.linkItems[0].name shouldBe "Business Account"
                    businessGuests.linkItems[0].linkSrc shouldBe
                        "https://www.dit.premierinn.digital/gb/en/business-booker/account/piba-registration.html?intcmp=bbfooter#/registration.html"
                    businessGuests.linkItems[4].name shouldBe "Inn Business FAQs"
                }

                expect("maps social and bottom links") {
                    result.body.socialMediaIcons shouldHaveSize 4
                    result.body.socialMediaIcons[0].label shouldBe "Facebook icon"
                    result.body.socialMediaIcons[0].linkSrc shouldBe "https://www.facebook.com/premierinn"
                    result.body.bottomLinks shouldHaveSize 4
                    result.body.bottomLinks[0].name shouldBe "InnBusiness Terms & Conditions"
                }

                expect("maps empty newsletter signup") {
                    val newsletterSignup = result.body.newsletterSignup.shouldNotBeNull()
                    newsletterSignup.newsletterText shouldBe null
                    newsletterSignup.signUpButtonText shouldBe null
                }
            }
        },
    )
