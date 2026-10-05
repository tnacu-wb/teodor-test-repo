package uk.co.whitbread.integrationtests.clients.payment

import uk.co.whitbread.integrationtests.clients.payment.model.MobileSdkInitRequest
import uk.co.whitbread.integrationtests.clients.payment.model.MobileSdkInitResponse
import uk.co.whitbread.integrationtests.clients.payment.model.SecureFieldsInitRequest
import uk.co.whitbread.integrationtests.clients.payment.model.SecureFieldsInitResponse
import uk.co.whitbread.integrationtests.framework.config.IntegrationTestConfig
import uk.co.whitbread.integrationtests.framework.http.ApiResult
import uk.co.whitbread.integrationtests.framework.http.ServiceApiClient

class PaymentOrchestrationApi(
    private val http: ServiceApiClient =
        IntegrationTestConfig.serviceApiClient(IntegrationTestConfig.config.paymentOrchestrationBaseUrl),
) {
    suspend fun initSecureFields(
        request: SecureFieldsInitRequest,
        testId: String,
    ): ApiResult<SecureFieldsInitResponse> = http.post("/api/payments/secure-fields", request, testId)

    suspend fun initMobileSdk(
        request: MobileSdkInitRequest,
        testId: String,
    ): ApiResult<MobileSdkInitResponse> = http.post("/api/payments/mobile-sdk", request, testId)
}
