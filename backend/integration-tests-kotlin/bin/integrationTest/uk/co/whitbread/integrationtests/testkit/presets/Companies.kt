package uk.co.whitbread.integrationtests.testkit.presets

import uk.co.whitbread.integrationtests.testkit.model.Company
import uk.co.whitbread.integrationtests.testkit.model.CompanyAddress

object Companies {
    val NEILL_TECHNICAL_SERVICES =
        Company(
            name = "Neill Technical Services",
            corpId = "1370",
            companyId = "2569623",
            telephoneNumber = "07496593260",
            address =
                CompanyAddress(
                    addressLine1 = "Unit 37f, Thistle Industrial Estate",
                    addressLine2 = "Church Street",
                    addressLine4 = "COWDENBEATH",
                    postalCode = "KY4 8LP",
                ),
        )

    val ACME_WITHOUT_NEGOTIATED_RATES =
        Company(
            name = "Acme Supplies Ltd",
            corpId = "2401",
            companyId = "3456789",
            telephoneNumber = "02079460000",
            negotiatedRateEnabled = false,
            address =
                CompanyAddress(
                    addressLine1 = "1 Test Street",
                    addressLine2 = "Test Park",
                    addressLine4 = "London",
                    postalCode = "SW1A 1AA",
                ),
        )
}
