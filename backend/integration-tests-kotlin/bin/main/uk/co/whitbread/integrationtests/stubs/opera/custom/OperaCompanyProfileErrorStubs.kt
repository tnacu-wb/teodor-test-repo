package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.answering
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.opera.companyProfiles
import uk.co.whitbread.integrationtests.stubs.opera.companyProfilesById
import uk.co.whitbread.integrationtests.testkit.model.Company

const val OPERA_COMPANY_PROFILE_BY_ID_REJECTED_STUB_ID = "opera.company-profile-by-id.rejected"
const val OPERA_COMPANY_PROFILE_REJECTED_STUB_ID = "opera.company-profile.rejected"
const val OPERA_COMPANY_PROFILE_EMPTY_BODY_STUB_ID = "custom.opera.company-profile-empty-body"

/** Builds an Opera rejection for the profile-by-company-id read (first call). */
fun companyProfileByIdFailure(company: Company): PlannedStub =
    companyProfilesById(listOf(company)).rejectedByOpera(
        id = OPERA_COMPANY_PROFILE_BY_ID_REJECTED_STUB_ID,
        detail = "Company profile could not be fetched.",
    )

/**
 * Builds a zero-length 200 for the profile-by-company-id read, installed with
 * `booking.opera.company-profile-by-id` excluded. Keeps the default's exact matcher (the six
 * repeated fetch instructions, authenticated hub headers, no `x-hotelid`), so the empty answer
 * lands on exactly the read the adapter would otherwise resolve.
 *
 * A zero-length body models Opera holding no profile at all under the company's id:
 * `bodyToMono` completes empty and the collected profile is null. A present-but-empty `{}`
 * body is a different world — it dereferences into the mappers and NPEs instead. This is the
 * profile-read counterpart of [getReservationEmpty], and it drives both
 * null-profile guards: errCode 51 on the company-rename branch and errCode 50 on the attach
 * guard.
 */
fun companyProfileByIdEmptyBody(company: Company): PlannedStub =
    companyProfilesById(listOf(company))
        .answering(OPERA_COMPANY_PROFILE_EMPTY_BODY_STUB_ID, jsonResponse(body = "", status = 200))

/** Builds an Opera rejection for the corporate-profile read (second call). */
fun companyProfileFailure(company: Company): PlannedStub =
    companyProfiles(listOf(company)).rejectedByOpera(
        id = OPERA_COMPANY_PROFILE_REJECTED_STUB_ID,
        detail = "Corporate profile could not be fetched.",
    )
