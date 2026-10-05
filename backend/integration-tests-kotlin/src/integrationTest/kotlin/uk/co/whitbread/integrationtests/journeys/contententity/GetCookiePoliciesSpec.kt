package uk.co.whitbread.integrationtests.journeys.contententity

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.contententity.ContentEntityApi
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Aem
import uk.co.whitbread.integrationtests.testkit.model.AemCookieDurationConfig
import uk.co.whitbread.integrationtests.testkit.model.AemCookieGroup
import uk.co.whitbread.integrationtests.testkit.model.AemCookieIntroView
import uk.co.whitbread.integrationtests.testkit.model.AemCookieManageView
import uk.co.whitbread.integrationtests.testkit.model.AemCookiePolicies
import uk.co.whitbread.integrationtests.testkit.model.Booking

private val cookiePoliciesAem =
    AemCookiePolicies(
        country = "gb",
        language = "en",
        brand = "pi",
        version = "1",
        config =
            AemCookieDurationConfig(
                cookieOptOutExpiryDays = "30",
                cookieOptInExpiryDays = "365",
            ),
        introView =
            AemCookieIntroView(
                title = "Cookies and how we use them",
                description =
                    "Collecting cookies helps us improve our website, keep everything secure and personalise " +
                        "your experience by tailoring content just for you. If you\u2019re happy with this, you can " +
                        "\u2018accept all cookies\u2019, or to find out more, click \u2018manage cookies\u2019.",
                manageButtonText = "Manage cookies",
                acceptAllButtonText = "Accept all cookies",
                necessaryOnlyButtonText = "Necessary only",
            ),
        manageView =
            AemCookieManageView(
                title = "Manage cookies",
                description =
                    "<p>Choose the cookies that work for you.</p>\n" +
                        "<p>If you need more information, please see our cookies notice.</p>\n",
                saveSettingsButtonText = "Confirm settings",
                alwaysActiveText = "Always Active",
                cookieGroup =
                    listOf(
                        AemCookieGroup(
                            cookieName = "permissionEssential",
                            title = "Essential",
                            description =
                                "<p>Some cookies are essential \u2013 our website wouldn\u2019t work without them! " +
                                    "We collect them to keep our website secure and ensure that from browsing to booking, " +
                                    "your online experience runs smoothly.</p>\n",
                            isAlwaysActive = true,
                            toggleLabel = "Essentials are always active.",
                        ),
                        AemCookieGroup(
                            cookieName = "permissionPerformance",
                            title = "Performance (Recommended) ",
                            description =
                                "<p>These cookies help us understand user experiences. We\u2019ll never use them " +
                                    "to identify you personally \u2013 just to monitor how well our website is working and if " +
                                    "there\u2019s any room for improvement.</p>\n",
                            isAlwaysActive = false,
                            toggleLabel = "Button to choose whether to allow or disallow us to collect performance cookies.",
                        ),
                        AemCookieGroup(
                            cookieName = "permissionExperience",
                            title = "Experience",
                            description =
                                "<p>We use these cookies to personalise your experience \u2013 tailoring content " +
                                    "throughout your visit. We also use these cookies to test new website features and improve " +
                                    "functionality across our website.</p>\n",
                            isAlwaysActive = false,
                            toggleLabel = "Button to choose whether to allow or disallow us to collect experience cookies.",
                        ),
                        AemCookieGroup(
                            cookieName = "permissionMarketing",
                            title = "Marketing",
                            description =
                                "<p>These cookies allow us to tailor the advertising you receive from Premier Inn. " +
                                    "Without these cookies you would still receive adverts \u2013 they would just be less relevant " +
                                    "to you.</p>\n",
                            isAlwaysActive = false,
                            toggleLabel =
                                "choose the cookies that work for you. If you need more information, " +
                                    "please see our cookies notice. ",
                        ),
                    ),
            ),
    )

class GetCookiePoliciesSpec :
    JourneySpec(
        "get cookie policies",
        {
            val contentEntityApi = ContentEntityApi()

            scenario("GET /v1/content/cookie-policies returns cookie policies from AEM") {
                val booking = Booking(aem = Aem(cookiePolicies = cookiePoliciesAem))

                installFor(booking)

                val result =
                    contentEntityApi.getCookiePolicies(
                        country = cookiePoliciesAem.country,
                        language = cookiePoliciesAem.language,
                        brand = cookiePoliciesAem.brand,
                        testId = testId,
                    )

                result.attachEvidence("Get Cookie Policies")

                expect("returns cookie policy metadata and expiry configuration from AEM") {
                    val cookiePolicies = result.body.cookiePolicies.shouldNotBeNull()
                    val config = cookiePolicies.config.shouldNotBeNull()

                    result.response.status.value shouldBe 200
                    cookiePolicies.version shouldBe cookiePoliciesAem.version
                    cookiePolicies.brand shouldBe cookiePoliciesAem.brand
                    config.cookieOptOutExpiryDays shouldBe cookiePoliciesAem.config.cookieOptOutExpiryDays
                    config.cookieOptInExpiryDays shouldBe cookiePoliciesAem.config.cookieOptInExpiryDays
                }

                expect("returns intro and manage view copy from AEM") {
                    val cookiePolicies = result.body.cookiePolicies.shouldNotBeNull()
                    val introView = cookiePolicies.introView.shouldNotBeNull()
                    val manageView = cookiePolicies.manageView.shouldNotBeNull()

                    introView.title shouldBe cookiePoliciesAem.introView.title
                    introView.description shouldBe cookiePoliciesAem.introView.description
                    introView.manageButtonText shouldBe cookiePoliciesAem.introView.manageButtonText
                    introView.acceptAllButtonText shouldBe cookiePoliciesAem.introView.acceptAllButtonText
                    introView.necessaryOnlyButtonText shouldBe cookiePoliciesAem.introView.necessaryOnlyButtonText

                    manageView.title shouldBe cookiePoliciesAem.manageView.title
                    manageView.description shouldBe cookiePoliciesAem.manageView.description
                    manageView.saveSettingsButtonText shouldBe cookiePoliciesAem.manageView.saveSettingsButtonText
                    manageView.alwaysActiveText shouldBe cookiePoliciesAem.manageView.alwaysActiveText
                }

                expect("returns all cookie groups from AEM") {
                    val cookiePolicies = result.body.cookiePolicies.shouldNotBeNull()
                    val manageView = cookiePolicies.manageView.shouldNotBeNull()

                    manageView.cookieGroup shouldHaveSize cookiePoliciesAem.manageView.cookieGroup.size
                    manageView.cookieGroup.map { it.cookieName } shouldBe
                        cookiePoliciesAem.manageView.cookieGroup.map { it.cookieName }
                    manageView.cookieGroup.map { it.title } shouldBe
                        cookiePoliciesAem.manageView.cookieGroup.map { it.title }
                    manageView.cookieGroup.map { it.isAlwaysActive } shouldBe
                        cookiePoliciesAem.manageView.cookieGroup.map { it.isAlwaysActive }
                    manageView.cookieGroup.map { it.toggleLabel } shouldBe
                        cookiePoliciesAem.manageView.cookieGroup.map { it.toggleLabel }
                }
            }

            scenario("GET /v1/content/cookie-policies passes mixed-case request values through to AEM") {
                val mixedCaseCookiePoliciesAem =
                    cookiePoliciesAem.copy(
                        country = "GB",
                        language = "EN",
                        brand = "PI",
                    )
                val booking = Booking(aem = Aem(cookiePolicies = mixedCaseCookiePoliciesAem))

                installFor(booking)

                val result =
                    contentEntityApi.getCookiePolicies(
                        country = mixedCaseCookiePoliciesAem.country,
                        language = mixedCaseCookiePoliciesAem.language,
                        brand = mixedCaseCookiePoliciesAem.brand,
                        testId = testId,
                    )

                result.attachEvidence("Get Cookie Policies Mixed Case")

                expect("returns cookie policies from the exact mixed-case AEM path") {
                    val cookiePolicies = result.body.cookiePolicies.shouldNotBeNull()
                    val config = cookiePolicies.config.shouldNotBeNull()

                    result.response.status.value shouldBe 200
                    cookiePolicies.version shouldBe mixedCaseCookiePoliciesAem.version
                    cookiePolicies.brand shouldBe mixedCaseCookiePoliciesAem.brand
                    config.cookieOptOutExpiryDays shouldBe
                        mixedCaseCookiePoliciesAem.config.cookieOptOutExpiryDays
                    config.cookieOptInExpiryDays shouldBe
                        mixedCaseCookiePoliciesAem.config.cookieOptInExpiryDays
                }
            }

            scenario("GET /v1/content/cookie-policies rejects missing required country before downstream calls") {
                // AEM is fully stubbed so a call would succeed. Zero recorded calls therefore
                // proves the request was rejected during validation, rather than proving only
                // that an unstubbed AEM happened to fail.
                val booking = Booking(aem = Aem(cookiePolicies = cookiePoliciesAem))

                installFor(booking)

                val result =
                    contentEntityApi.getCookiePolicies(
                        country = null,
                        language = cookiePoliciesAem.language,
                        brand = cookiePoliciesAem.brand,
                        testId = testId,
                    )

                result.attachEvidence("Get Cookie Policies Missing Country")

                expect("returns a validation error without calling AEM") {
                    result.response.status.value shouldBe 422
                    result.bodyText.isNotBlank() shouldBe true
                    callCount(Upstream.AEM) shouldBe 0
                }
            }

            scenario("GET /v1/content/cookie-policies rejects missing required language before downstream calls") {
                val booking = Booking(aem = Aem(cookiePolicies = cookiePoliciesAem))

                installFor(booking)

                val result =
                    contentEntityApi.getCookiePolicies(
                        country = cookiePoliciesAem.country,
                        language = null,
                        brand = cookiePoliciesAem.brand,
                        testId = testId,
                    )

                result.attachEvidence("Get Cookie Policies Missing Language")

                expect("returns a validation error without calling AEM") {
                    result.response.status.value shouldBe 422
                    result.bodyText.isNotBlank() shouldBe true
                    callCount(Upstream.AEM) shouldBe 0
                }
            }

            scenario("GET /v1/content/cookie-policies rejects missing required brand before downstream calls") {
                val booking = Booking(aem = Aem(cookiePolicies = cookiePoliciesAem))

                installFor(booking)

                val result =
                    contentEntityApi.getCookiePolicies(
                        country = cookiePoliciesAem.country,
                        language = cookiePoliciesAem.language,
                        brand = null,
                        testId = testId,
                    )

                result.attachEvidence("Get Cookie Policies Missing Brand")

                expect("returns a validation error without calling AEM") {
                    result.response.status.value shouldBe 422
                    result.bodyText.isNotBlank() shouldBe true
                    callCount(Upstream.AEM) shouldBe 0
                }
            }
        },
    )
