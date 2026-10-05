package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.opera.promotionCodes
import uk.co.whitbread.integrationtests.testkit.model.Hotel

const val OPERA_PROMOTION_CODES_REJECTED_STUB_ID = "opera.promotion-codes.rejected"

/** Builds an Opera rejection for the promotion-codes read, installed with the default excluded. */
fun promotionCodesFailure(hotel: Hotel): PlannedStub =
    promotionCodes(listOf(hotel)).rejectedByOpera(
        id = OPERA_PROMOTION_CODES_REJECTED_STUB_ID,
        detail = "Promotion codes could not be fetched.",
    )
