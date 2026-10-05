package uk.co.whitbread.integrationtests.clients.payment.model

import kotlinx.serialization.Serializable

/**
 * Body of `POST /api/payments/mobile-sdk`.
 *
 * @param basketId basket identifier returned as `basketReference` by create-reservation. The
 * service validates it against `^[A-Z]{3}-{uuid}$`, which is basket-service's basket id. There is
 * no `returnUrl` on this channel: the native SDK carries 3-D Secure rather than a browser redirect.
 * @param country locale country the payment methods are resolved for, lower case.
 * @param language locale language the payment methods are resolved for, lower case.
 * @param userType `LEISURE` or `BUSINESS`; selects which rule set the payment methods service runs.
 * @param clientChannel the calling channel, `APPS_IOS` or `APPS_ANDROID` for native.
 *
 * All five are required: the orchestrator rejects a blank one with `400 INVALID_REQUEST` before the
 * workflow starts. The last four become the `GET /v1/payment-methods` query the workflow makes
 * against payment-methods-entity-service, which must offer `CARD`/`NEW_CARD` before Datatrans is
 * ever called.
 */
@Serializable
data class MobileSdkInitRequest(
    val basketId: String,
    val country: String,
    val language: String,
    val userType: String,
    val clientChannel: String,
)

/** Response of `POST /api/payments/mobile-sdk`. */
@Serializable
data class MobileSdkInitResponse(
    val transactionId: String,
)
