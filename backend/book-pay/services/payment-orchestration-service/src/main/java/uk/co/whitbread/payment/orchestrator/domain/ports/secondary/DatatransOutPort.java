package uk.co.whitbread.payment.orchestrator.domain.ports.secondary;

import uk.co.whitbread.payment.orchestrator.domain.exceptions.DatatransGatewayException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.ServiceUnavailableException;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransAuthorizeResponse;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransMobileSdkRequest;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransSecureFieldsRequest;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransTransactionStatus;

/**
 * Secondary port for Datatrans payment gateway integration.
 *
 * <p>Defines the outbound contract for initializing and authorizing
 * payment transactions through the Datatrans gateway.
 */
public interface DatatransOutPort {

  /**
   * Authorize a previously initialized transaction.
   *
   * @param transactionId the Datatrans transaction identifier
   * @param refno the merchant reference number
   * @param amount the transaction amount in minor units (required by v2 authorize)
   * @param merchantId the merchant ID to authenticate as (must match the merchant used at init)
   * @return the authorization response containing card data and status
   */
  DatatransAuthorizeResponse authorizeTransaction(String transactionId, String refno,
      long amount, String merchantId);

  /**
   * Retrieve the current status of a Datatrans transaction.
   *
   * @param transactionId the Datatrans transaction identifier
   * @param merchantId the merchant ID to authenticate as
   * @return the current transaction status
   */
  DatatransTransactionStatus getTransactionStatus(String transactionId, String merchantId);

  /**
   * Initialize a Secure Fields transaction with Datatrans.
   *
   * @param request the Datatrans request containing amount, currency, returnUrl, and autoSettle
   * @return the Datatrans transaction identifier (valid for 30 minutes)
   * @throws DatatransGatewayException if Datatrans returns a non-2xx response
   * @throws ServiceUnavailableException if Datatrans is unreachable
   */
  default String initSecureFields(DatatransSecureFieldsRequest request) {
    throw new UnsupportedOperationException("initSecureFields not yet implemented");
  }

  /**
   * Initialize a Mobile SDK transaction with Datatrans v2 API.
   *
   * @param request the v2 request containing amount, currency, refno, paymentMethods
   * @return the transaction ID (UUID string)
   * @throws DatatransGatewayException if Datatrans returns a non-2xx response
   * @throws ServiceUnavailableException if Datatrans is unreachable
   */
  String initMobileSdk(DatatransMobileSdkRequest request);

  /**
   * Settle a previously authorized transaction using Datatrans v2 API.
   *
   * <p>Calls {@code POST /v2/transactions/{transactionId}/settle} on the Datatrans gateway.
   *
   * @param transactionId the Datatrans transaction identifier
   * @param amount the settlement amount in minor units
   * @param currency the currency code (e.g. "GBP")
   * @param refno the merchant reference number
   * @param merchantId the merchant ID to authenticate as
   * @throws DatatransGatewayException if Datatrans returns a non-2xx response
   * @throws ServiceUnavailableException if Datatrans is unreachable
   */
  void settleTransaction(String transactionId, long amount, String currency, String refno,
      String merchantId);

  /**
   * Cancel a previously authorized transaction using Datatrans v2 API.
   *
   * <p>Calls {@code POST /v2/transactions/{transactionId}/cancel} on the Datatrans gateway.
   * The request body is empty; authentication is HTTP Basic Auth using the merchant credentials.
   * A successful cancellation returns {@code 204 No Content}.
   *
   * @param transactionId the Datatrans transaction identifier
   * @param merchantId the merchant ID to authenticate as (must match the merchant used at init)
   * @throws DatatransGatewayException if Datatrans returns a non-2xx response (e.g. 400 when the
   *     transaction cannot be cancelled due to invalid state)
   * @throws ServiceUnavailableException if Datatrans is unreachable
   */
  void cancelTransaction(String transactionId, String merchantId);
}
