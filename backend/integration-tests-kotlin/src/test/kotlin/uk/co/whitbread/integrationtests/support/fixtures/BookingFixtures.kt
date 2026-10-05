package uk.co.whitbread.integrationtests.support.fixtures

import uk.co.whitbread.integrationtests.testkit.model.Aem
import uk.co.whitbread.integrationtests.testkit.model.AemFooter
import uk.co.whitbread.integrationtests.testkit.model.AemSite
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.Company
import uk.co.whitbread.integrationtests.testkit.model.CompanyAddress

/** Smallest Booking that activates the AEM planner and nothing else. */
internal fun aemBooking(): Booking =
    Booking(
        aem =
            Aem(
                footer =
                    AemFooter(
                        country = "gb",
                        language = "en",
                        site = AemSite.BUSINESS_BOOKER,
                    ),
            ),
    )

/**
 * Smallest Booking that activates the CDH and Opera company planners.
 *
 * [name] is a parameter rather than a constant because each caller keeps the company name it
 * already used, so consolidating these fixtures changes no installed mapping body.
 */
internal fun companyBooking(name: String): Booking =
    Booking(
        companies =
            listOf(
                Company(
                    name = name,
                    corpId = "1234",
                    companyId = "company-1234",
                    telephoneNumber = "+441234567890",
                    address = CompanyAddress(addressLine1 = "1 Test Street", postalCode = "AB1 2CD"),
                ),
            ),
    )
