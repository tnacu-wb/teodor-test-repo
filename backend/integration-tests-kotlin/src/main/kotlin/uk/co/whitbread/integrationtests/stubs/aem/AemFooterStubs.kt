package uk.co.whitbread.integrationtests.stubs.aem

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.testkit.model.AemFooter
import uk.co.whitbread.integrationtests.testkit.model.AemSite

const val AEM_FOOTER_STUB_ID = "booking.aem.footer"

fun footer(footer: AemFooter): PlannedStub =
    PlannedStub(
        id = AEM_FOOTER_STUB_ID,
        target = WireMockTarget.AEM,
        mappings = listOf(footerMapping(footer)),
    )

private fun footerMapping(footer: AemFooter): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "GET",
                url = "/${footer.country}/${footer.language}/index.footer.data/site/${footer.site.value}.json",
            ),
        response =
            jsonResponse(
                body =
                    when (footer.site) {
                        AemSite.BUSINESS_BOOKER -> businessBookerFooterBody()
                        else -> error("No AEM footer fixture is available for site ${footer.site.value}")
                    },
            ),
    )

private fun businessBookerFooterBody(): String =
    """
    {
      "linkTabs": [
        {
          "tabTitle": "Business Booker",
          "introTitle": "",
          "introDescription": "",
          "linkColumns": [
            {
              "columnTitle": "About us",
              "linkItems": [
                {
                  "linkText": "Why we're Premier",
                  "linkPath": "https://www.dit.premierinn.digital/gb/en/business-booker/why.html?intcmp=bbfooter.html",
                  "linkOpenNewTab": "false"
                },
                {
                  "linkText": "Our rates",
                  "linkPath": "https://www.dit.premierinn.digital/gb/en/business-booker/why/rates.html?intcmp=bbfooter.html",
                  "linkOpenNewTab": "false"
                },
                {
                  "linkText": "Premier Inn CleanProtect\u2122",
                  "linkPath": "https://www.dit.premierinn.digital/gb/en/business-booker/why/cleanliness.html?intcmp=bbfooter.html",
                  "linkOpenNewTab": "false"
                },
                {
                  "linkText": "Force for Good",
                  "linkPath": "https://www.dit.premierinn.digital/gb/en/business-booker/why/force-for-good.html?intcmp=bbfooter.html",
                  "linkOpenNewTab": "false"
                },
                {
                  "linkText": "Great Ormond Street",
                  "linkPath": "https://www.dit.premierinn.digital/gb/en/business-booker/why/gosh-childrens-charity.html?intcmp=bbfooter.html",
                  "linkOpenNewTab": "false"
                }
              ]
            },
            {
              "columnTitle": "Business guests",
              "linkItems": [
                {
                  "linkText": "Business Account",
                  "linkPath": "https://www.dit.premierinn.digital/gb/en/business-booker/account/piba-registration.html?intcmp=bbfooter#/registration.html",
                  "linkOpenNewTab": "false"
                },
                {
                  "linkText": "Business Account FAQs",
                  "linkPath": "https://www.dit.premierinn.digital/gb/en/business-booker/faq/business-account-help.html?intcmp=bbfooter.html",
                  "linkOpenNewTab": "false"
                },
                {
                  "linkText": "Meeting rooms",
                  "linkPath": "https://www.premiermeetings.co.uk/?intcmp=bbfooter",
                  "linkOpenNewTab": "false"
                },
                {
                  "linkText": "Group bookings",
                  "linkPath": "https://www.dit.premierinn.digital/gb/en/business-booker/why/groups.html?intcmp=bbfooter.html",
                  "linkOpenNewTab": "false"
                },
                {
                  "linkText": "Inn Business FAQs",
                  "linkPath": "https://www.dit.premierinn.digital/gb/en/business-booker/faq/business-booker.html?intcmp=bbfooter.html",
                  "linkOpenNewTab": "false"
                }
              ]
            },
            {
              "columnTitle": "Legal",
              "linkItems": [
                {
                  "linkText": "Terms and conditions",
                  "linkPath": "https://www.dit.premierinn.digital/gb/en/business-booker/terms/booking-terms-and-conditions.html?intcmp=bbfooter.html",
                  "linkOpenNewTab": "false"
                },
                {
                  "linkText": "Inn Business T&Cs",
                  "linkPath": "https://www.dit.premierinn.digital/gb/en/business-booker/terms/terms-and-conditions.html?intcmp=bbfooter.html",
                  "linkOpenNewTab": "false"
                },
                {
                  "linkText": "Privacy notice",
                  "linkPath": "https://www.dit.premierinn.digital/gb/en/business-booker/terms/privacy-policy.html?intcmp=bbfooter.html",
                  "linkOpenNewTab": "false"
                },
                {
                  "linkText": "Cookies notice",
                  "linkPath": "https://www.dit.premierinn.digital/gb/en/business-booker/terms/how-we-use-cookies.html?intcmp=bbfooter.html",
                  "linkOpenNewTab": "false"
                },
                {
                  "linkText": "Terms of use",
                  "linkPath": "https://www.dit.premierinn.digital/gb/en/business-booker/terms/terms-of-use.html?intcmp=bbfooter.html",
                  "linkOpenNewTab": "false"
                }
              ]
            },
            {
              "columnTitle": "Our hotels",
              "linkItems": [
                {
                  "linkText": "Our rooms",
                  "linkPath": "https://www.dit.premierinn.digital/gb/en/business-booker/sleep/our-rooms.html?intcmp=bbfooter.html",
                  "linkOpenNewTab": "false"
                },
                {
                  "linkText": "Food & drink",
                  "linkPath": "https://www.dit.premierinn.digital/gb/en/business-booker/why/food.html?intcmp=bbfooter.html",
                  "linkOpenNewTab": "false"
                },
                {
                  "linkText": "Buy our bed",
                  "linkPath": "https://www.dit.premierinn.digital/gb/en/business-booker/sleep/buy-our-bed.html?intcmp=bbfooter.html",
                  "linkOpenNewTab": "false"
                },
                {
                  "linkText": "Disabled Access",
                  "linkPath": "https://www.dit.premierinn.digital/gb/en/business-booker/terms/disabled-access.html?intcmp=bbfooter.html",
                  "linkOpenNewTab": "false"
                },
                {
                  "linkText": "Wi-Fi",
                  "linkPath": "https://www.dit.premierinn.digital/gb/en/business-booker/why/wifi.html?intcmp=bbfooter.html",
                  "linkOpenNewTab": "false"
                }
              ]
            },
            {
              "columnTitle": "",
              "linkItems": []
            },
            {
              "columnTitle": "",
              "linkItems": []
            }
          ]
        }
      ],
      "socialLinks": [
        {
          "linkUrl": "https://www.facebook.com/premierinn",
          "iconText": "Facebook icon",
          "iconUrl": "/content/dam/icons/resources/social/facebook-dark-square-small.svg"
        },
        {
          "linkUrl": "https://twitter.com/premierinn",
          "iconText": "Twitter icon",
          "iconUrl": "/content/dam/icons/resources/social/2024-social-x-small.svg"
        },
        {
          "linkUrl": "https://www.instagram.com/premierinn",
          "iconText": "Instagram icon",
          "iconUrl": "/content/dam/icons/resources/social/2024-social-instagram-small.svg"
        },
        {
          "linkUrl": "https://www.linkedin.com/company/premier-inn",
          "iconText": "LinkedIn icon",
          "iconUrl": "/content/dam/icons/resources/social/2024-social-linkedin-small.svg"
        }
      ],
      "copyright": "&copy; 2026 Premier Inn",
      "bottomLinks": [
        {
          "linkText": "InnBusiness Terms & Conditions",
          "linkPath": "/gb/en/a.html",
          "linkOpenNewTab": "false"
        },
        {
          "linkText": "Privacy",
          "linkPath": "/gb/en/b.html",
          "linkOpenNewTab": "false"
        },
        {
          "linkText": "Cookies",
          "linkPath": "/gb/en/b.html",
          "linkOpenNewTab": "false"
        },
        {
          "linkText": "Terms of use",
          "linkPath": "/gb/en/d.html",
          "linkOpenNewTab": "false"
        }
      ],
      "newsletterSignup": {}
    }
    """.trimIndent()
