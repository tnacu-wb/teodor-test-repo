package uk.co.whitbread.payment.orchestrator.domain.workflow;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import uk.co.whitbread.payment.orchestrator.domain.model.BasketStatus;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransAuthorizeResponse;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentOption;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransMobileSdkRequest;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransSecureFieldsRequest;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransTransactionStatus;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentMethodValidationResult;
import uk.co.whitbread.payment.orchestrator.domain.model.Reservation;

/**
 * Temporal activity interface for external service calls during payment processing.
 *
 * <p>Defines the side-effecting operations invoked by payment workflows,
 * including transaction initialization, authorization, and deposit posting.
 */
@ActivityInterface
public interface PaymentActivities {

  /**
   * Authorize a previously initialized transaction.
   *
   * @param transactionId the transaction identifier to authorize
   * @param refno the merchant reference number
   * @param amount the transaction amount in minor units
   * @param merchantId the merchant ID to authenticate as (must match init merchant)
   * @return the authorization response containing card data and status
   */
  @ActivityMethod
  DatatransAuthorizeResponse authorizeTransaction(String transactionId, String refno,
      long amount, String merchantId);

  /**
   * Retrieve reservation data from the Basket Service.
   *
   * @param basketId the basket identifier
   * @return the reservation details
   */
  @ActivityMethod
  Reservation getReservation(String basketId);

  /**
   * Validate payment methods and get available card brands for a hotel.
   *
   * @param basketReference the basket identifier
   * @param hotelId the hotel identifier (for logging/tracing)
   * @param country the country code (e.g. "gb", "de")
   * @param language the language code (e.g. "en", "de")
   * @param userType the user type (e.g. "LEISURE", "BUSINESS")
   * @param clientChannel the client channel (e.g. "PI", "APPS_IOS")
   * @return validation result with availability and card brands
   */
  @ActivityMethod
  PaymentMethodValidationResult validatePaymentMethods(
      String basketReference, String hotelId, String country, String language,
      String userType, String clientChannel);

  /**
   * Initialize a Datatrans Secure Fields transaction.
   *
   * @param request the Secure Fields initialization request
   * @return the Datatrans transaction identifier
   */
  @ActivityMethod
  String initDatatransSecureFields(DatatransSecureFieldsRequest request);

  /**
   * Initialize a Mobile SDK transaction with Datatrans v2 API.
   *
   * @param request the Mobile SDK transaction request containing amount, currency, refno,
   *     and payment methods
   * @return the Datatrans transaction identifier (UUID string)
   */
  @ActivityMethod
  String initMobileSdkTransaction(DatatransMobileSdkRequest request);

  /**
   * Retrieve the current status of a Datatrans transaction.
   *
   * @param transactionId the Datatrans transaction identifier
   * @param merchantId the merchant ID to authenticate as
   * @return the current Datatrans transaction status
   */
  @ActivityMethod
  DatatransTransactionStatus getTransactionStatus(String transactionId, String merchantId);

  /**
   * Settle a previously authorized transaction with Datatrans v2 API (deferred settlement).
   *
   * @param transactionId the Datatrans transaction identifier to settle
   * @param amount the settlement amount in minor units
   * @param currency the currency code (e.g. "GBP")
   * @param refno the merchant reference number
   * @param merchantId the merchant ID to authenticate as
   */
  @ActivityMethod
  void settleTransaction(String transactionId, long amount, String currency, String refno,
      String merchantId);

  /**
   * Publish the {@code PaymentAuthorisedEvent} to Kafka for a payment that has just reached
   * {@code AUTHORIZED}.
   *
   * <p>This activity constructs and publishes a {@code PaymentAuthorisedEvent} to the
   * {@code payment-authorised} Kafka topic. The event is consumed by the Basket Service to
   * drive the booking-confirmation choreography. Both the Mobile SDK and Secure Fields
   * workflows call this activity at the same logical position (after AUTHORIZED).
   *
   * <p><strong>Delivery is at-least-once.</strong> The workflow retries this activity until the
   * publish is acknowledged, and a publish that reached Kafka but whose acknowledgement was lost
   * (a broker timeout, a worker crash between send and ack) is retried and lands a second copy
   * of the same event. Consumers must therefore deduplicate on {@code basketId} /
   * {@code transactionId} and treat a repeated event as a no-op. The alternative — publishing at
   * most once — silently strands authorized payments whose booking is never triggered, which is
   * the worse failure.
   *
   * <p>{@code cardAlias} is a Datatrans card token and is PII-adjacent — it must never be
   * logged. {@code last4Digits} and {@code expiry} are derived from the Datatrans card info
   * and are safe for inclusion in the event payload but must not appear in log statements.
   *
   * @param basketId the basket identifier (also the Kafka message key and workflow
   *     correlation key)
   * @param transactionId the authorized Datatrans transaction identifier
   * @param cardAlias the tokenised card alias returned by Datatrans, may be {@code null}
   * @param authorizedAmount the authorized amount in minor units
   * @param currency the payment currency code (e.g. "GBP")
   * @param paymentMethod the Datatrans payment method code (e.g. "VIS", "ECA"),
   *     may be {@code null}
   * @param last4Digits the last 4 digits of the masked card number, may be {@code null}
   * @param expiry the card expiry in "MM/YY" format, may be {@code null}
   * @param paymentOption the payment option (e.g. PAY_NOW)
   * @param language the customer's language as sent by the frontend on init (e.g. "en", "de"),
   *     may be {@code null}
   */
  @ActivityMethod
  void publishAuthorisedPaymentEvent(String basketId, String transactionId, String cardAlias,
      long authorizedAmount, String currency, String paymentMethod, String last4Digits,
      String expiry, PaymentOption paymentOption, String language);

  /**
   * Cancel a previously authorized transaction with Datatrans v2 API.
   *
   * <p>Calls {@code POST /v2/transactions/{transactionId}/cancel} to void an authorized
   * payment that should not be settled (e.g. when the downstream booking confirmation fails).
   * The request uses HTTP Basic Auth with the given merchant ID.
   *
   * <p>A successful cancellation returns {@code 204 No Content} from Datatrans. On failure,
   * the activity may throw:
   * <ul>
   *   <li>{@code TransactionNotFoundException} — if the transaction does not exist or has
   *       already been settled (HTTP 404)</li>
   *   <li>{@code DatatransGatewayException} — if the transaction cannot be cancelled due to
   *       an invalid state or a gateway error (HTTP 400 or 5xx)</li>
   * </ul>
   * Temporal will retry transient failures according to the activity retry policy.
   *
   * @param transactionId the Datatrans transaction identifier to cancel
   * @param merchantId the merchant ID to authenticate as (must match the merchant used at init)
   */
  @ActivityMethod
  void cancelTransaction(String transactionId, String merchantId);

  /**
   * Resolve the Datatrans merchant ID for the given hotel code.
   *
   * @param hotelCode the hotel identifier (e.g. "HARHOR", "GRESOU")
   * @return the resolved Datatrans merchant ID (e.g. "deWB-HARHOR")
   */
  @ActivityMethod
  String resolveMerchantId(String hotelCode);

  /**
   * Read the current basket status from the Basket Service.
   *
   * <p>Calls {@code GET /v1/baskets/{basket-reference}} and returns the basket's lifecycle
   * status. This is the safety net behind the {@code bookingCompleted} Kafka signal: if the
   * signal is lost, delayed, or delivered to a workflow that had not yet reached
   * {@code AUTHORIZED}, the workflow would otherwise wait forever on an authorization that is
   * quietly holding the guest's money.
   *
   * @param basketId the basket identifier (the basket reference)
   * @return the current basket status, or {@link BasketStatus#UNKNOWN} when the Basket Service
   *     reports a status this service does not recognise
   */
  @ActivityMethod
  BasketStatus getBasketStatus(String basketId);

  /**
   * Change the basket status via the Basket Service.
   *
   * <p>Calls {@code PUT /v1/baskets/{bookingReference}/changeStatus} to transition the
   * basket to the specified status. Must be called after obtaining the Datatrans transaction
   * ID but before returning it to the frontend, ensuring the basket is in {@code PAY_PENDING}
   * state before payment authorisation begins.
   *
   * <p>Failure to change status is fatal — the workflow should not proceed with payment
   * if the basket cannot be transitioned.
   *
   * @param bookingReference the booking reference (3-letter + 7-digit format, e.g. "ARH1234567")
   * @param status the target basket status (e.g. "PAY_PENDING")
   */
  @ActivityMethod
  void changeBasketStatus(String bookingReference, String status);
}
