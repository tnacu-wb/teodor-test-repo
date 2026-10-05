package uk.co.whitbread.integrationtests.stubs.worldline

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.BodyPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.escapeMarkupText
import uk.co.whitbread.integrationtests.stubs.xmlResponse
import uk.co.whitbread.integrationtests.testkit.model.TetheredAccount

const val WORLDLINE_TETHERED_USER_DETAILS_STUB_ID = "booking.worldline.tethered-user-details"

/** Builds a scoped Worldline tethered-user mapping with safely encoded XML text. */
fun tetheredUserDetails(tetheredAccount: TetheredAccount): PlannedStub =
    PlannedStub(
        id = WORLDLINE_TETHERED_USER_DETAILS_STUB_ID,
        target = WireMockTarget.WORLDLINE,
        mappings = listOf(tetheredUserDetailsMapping(tetheredAccount)),
    )

private fun tetheredUserDetailsMapping(tetheredAccount: TetheredAccount): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "POST",
                urlPath = "/b2b.pi.v1.1/b2bpiapi.svc",
                bodyPatterns =
                    listOf(
                        BodyPattern(contains = "tetheredUserDetailsGet"),
                        BodyPattern(contains = "{${tetheredAccount.tetheredUserGuid}}"),
                    ),
            ),
        response =
            xmlResponse(
                body = tetheredUserDetailsResponse(tetheredAccount),
            ),
    )

/** Builds the SOAP response while escaping every dynamic XML text node. */
private fun tetheredUserDetailsResponse(tetheredAccount: TetheredAccount): String =
    """
    <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
      <s:Body>
        <tetheredUserDetailsGetResponse xmlns="worldline.mst.bsm.api.b2b.pi.messages.v1.1">
          <Response>
            <ResultCode xmlns="worldline.mst.bsm.api.b2b.pi.data.v1.1">OK</ResultCode>
            <MetaInfo xmlns="worldline.mst.bsm.api.b2b.pi.data.v1.1">
              <MessageGuid>mock-message-guid</MessageGuid>
              <TimeStamp>2026-06-29T00:00:00Z</TimeStamp>
            </MetaInfo>
            <TetheredUserDetails xmlns="worldline.mst.bsm.api.b2b.pi.data.v1.1">
              <TetheredUserOverview>
                <TetheredUserGuid>${escapeMarkupText(tetheredAccount.tetheredUserGuid)}</TetheredUserGuid>
                <APIUserGuid>mock-api-user-guid</APIUserGuid>
                <UserRole>${escapeMarkupText(tetheredAccount.userRole.worldlineValue)}</UserRole>
                <CountMyCards>${tetheredAccount.countMyCards}</CountMyCards>
              </TetheredUserOverview>
              <CustomerAccountOverview>
                <PrimarySchemeCustomerId>123</PrimarySchemeCustomerId>
                <SchemeCustomerId>123</SchemeCustomerId>
                <AccountName>Integration Test Account</AccountName>
                <AccountNumber>${escapeMarkupText(tetheredAccount.pibaAccountId)}</AccountNumber>
              </CustomerAccountOverview>
            </TetheredUserDetails>
          </Response>
        </tetheredUserDetailsGetResponse>
      </s:Body>
    </s:Envelope>
    """.trimIndent()
