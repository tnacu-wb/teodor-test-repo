package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.opera.companyNegotiatedRates
import uk.co.whitbread.integrationtests.testkit.model.Company

const val OPERA_COMPANY_NEGOTIATED_RATES_REJECTED_STUB_ID =
    "custom.opera.company-negotiated-rates.rejected"

/** Builds an Opera not-found rejection for one company's negotiated-rate read. */
fun companyNegotiatedRatesNotFound(company: Company): PlannedStub =
    companyNegotiatedRates(company).rejectedByOpera(
        id = OPERA_COMPANY_NEGOTIATED_RATES_REJECTED_STUB_ID,
        detail = "Negotiated rates could not be fetched.",
        status = 404,
    )
