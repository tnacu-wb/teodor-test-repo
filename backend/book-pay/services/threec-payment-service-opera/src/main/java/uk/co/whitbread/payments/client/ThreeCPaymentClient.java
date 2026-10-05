package uk.co.whitbread.payments.client;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.ClientTokenRequest;
import com.braintreegateway.Customer;
import com.braintreegateway.CustomerRequest;
import com.braintreegateway.Environment;
import com.braintreegateway.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.ObjectAppendingMarker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.config.PaypalAccountName;
import uk.co.whitbread.payments.config.PaypalConfig;
import uk.co.whitbread.payments.config.PaypalToken;
import uk.co.whitbread.payments.converters.ThreeCTransformer;
import uk.co.whitbread.payments.exception.ErrorCode;
import uk.co.whitbread.payments.exception.ErrorCodes;
import uk.co.whitbread.payments.exception.PaymentProcessingException;
import uk.co.whitbread.payments.exception.PaymentServiceException;
import uk.co.whitbread.payments.model.*;
import uk.co.whitbread.payments.model.dynamo.PaymentsSchema;
import uk.co.whitbread.payments.model.threec.CreateTokenResponse;
import uk.co.whitbread.payments.model.threec.InitialiseRequest;
import uk.co.whitbread.payments.model.threec.InitialiseResponse;
import uk.co.whitbread.payments.model.threec.NoCardReadTransactionRequest;
import uk.co.whitbread.payments.model.threec.NoCardReadTransactionResponse;
import uk.co.whitbread.payments.model.threec.PaymentProviderTransactionRequest;
import uk.co.whitbread.payments.model.threec.PaymentProviderTransactionResponse;
import uk.co.whitbread.payments.model.threec.PaypalForwardAPITransactionRequest;
import uk.co.whitbread.payments.model.threec.PaypalForwardAPITransactionResponse;
import uk.co.whitbread.payments.model.threec.PaypalForwardAPITransactionResponseBody;
import uk.co.whitbread.payments.model.threec.ReverseByTransactionIdRequest;
import uk.co.whitbread.payments.model.threec.ReverseByTransactionIdResponse;
import uk.co.whitbread.payments.model.threec.StartReconciliationRequest;
import uk.co.whitbread.payments.model.threec.StartReconciliationResponse;
import uk.co.whitbread.payments.model.threec.UpdateTokenResponse;
import uk.co.whitbread.payments.properties.ProviderAccount;
import uk.co.whitbread.payments.properties.ThreeCProperties;
import uk.co.whitbread.payments.service.EMerchantService;
import uk.co.whitbread.payments.service.PaypalTokenService;
import uk.co.whitbread.payments.util.ThreeCResponseUtil;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Service
public class ThreeCPaymentClient {

  private final ObjectMapper mapper;
  private final ThreeCProperties threeCProperties;
  private final ThreeCTransformer threeCTransformer;
  private final WebClient webClient;
  private final PaypalTokenService paypalTokenService;
  private final EMerchantService eMerchantService;
  private final PaypalConfig paypalConfig;
  private static final Map<String, PaypalToken> TOKENS = new ConcurrentHashMap<>();

  ThreeCPaymentClient(@Qualifier("threeC") WebClient webClient,
      ObjectMapper mapper,
      ThreeCProperties threeCProperties, ThreeCTransformer threeCTransformer,
      EMerchantService eMerchantService, PaypalConfig paypalConfig,
      PaypalTokenService paypalTokenService) {
    this.webClient = webClient;
    this.mapper = mapper;
    this.threeCProperties = threeCProperties;
    this.threeCTransformer = threeCTransformer;
    this.eMerchantService = eMerchantService;
    this.paypalConfig = paypalConfig;
    this.paypalTokenService = paypalTokenService;
  }

  /**
   * Sets up a hosted iPage session with 3C.
   *
   * @param paymentRequest the incoming payment request
   * @param paymentId      the unique payment resource identifier
   * @return A mono representing the 3C initialise response
   */
  @Timed(description = "time taken to retrieve iPage session from 3C", value = "3c.get.ipage.session")
  public Mono<InitialiseResponse> initialiseIpage(PaymentRequest paymentRequest, String paymentId,
      String cardTemplate, ProviderAccount account) {
    InitialiseRequest initialiseRequest = threeCTransformer.populateInitialiseRequest(
        paymentRequest, paymentId, cardTemplate, account);
    return getInitialiseResponseMono(initialiseRequest);
  }

  /**
   * Sets up a hosted iPage session with 3C.
   *
   * @param saveCardRequest the incoming payment request
   * @param paymentId      the unique payment resource identifier
   * @return A mono representing the 3C initialise response
   */
  @Timed(description = "time taken to retrieve iPage session from 3C", value = "3c.get.ipage.session")
  public Mono<InitialiseResponse> initialiseIpage(SaveCardRequest saveCardRequest, String paymentId,
      String cardTemplate, ProviderAccount account) {
    InitialiseRequest initialiseRequest = threeCTransformer.populateInitialiseRequest(
        saveCardRequest, paymentId, cardTemplate, account);
    return getInitialiseResponseMono(initialiseRequest);
  }

  @Timed(description = "time taken to retrieve iPage session from 3C", value = "3c.get.ipage.session")
  public Mono<InitialiseResponse> initialiseAuthorizeScaIpage(AuthorizeScaRequest authorizeScaRequest, String paymentId,
                                                              ProviderAccount account) {
    InitialiseRequest initialiseRequest = threeCTransformer.populateInitialiseAuthorizeScaRequest(
            paymentId, account, authorizeScaRequest.getLanguage(), authorizeScaRequest.getCountry());
    return getInitialiseResponseMono(initialiseRequest);
  }

  private Mono<InitialiseResponse> getInitialiseResponseMono(InitialiseRequest initialiseRequest) {
    log.info("Sending initialise iPage request to {}. with temp request {}",
        threeCProperties.getEndpoints().getInitialise(), initialiseRequest);
    return webClient
        .post()
        .uri(threeCProperties.getEndpoints().getInitialise())
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .body(BodyInserters.fromValue(initialiseRequest))
        .retrieve()
        .bodyToMono(String.class)
        .map(ThreeCResponseUtil::cleanUpResponse)
        .flatMap(response -> {
          try {
            return Mono.just(mapper.readValue(response, InitialiseResponse.class));
          } catch (Exception e) {
            log.error("Could not map Initialise response: {}, error: {}", response, e.getMessage());
            throw new PaymentServiceException(HttpStatus.INTERNAL_SERVER_ERROR,
                String.format("Could not map Initialise response: %s, error: %s", response,
                    e.getMessage()), ErrorCodes.UNABLE_TO_PARSE_PROVIDER_RESPONSE);
          }
        });
  }

  /**
   * Executes a no card read transaction via 3C Direct API (MOTO).
   *
   * @param paymentRequest the incoming payment request
   * @param paymentId      the unique payment resource identifier
   * @return A mono representing the 3C no card read API response
   */
  @Timed(description = "time taken to retrieve transaction response from 3C", value = "3c.get.moto")
  public Mono<NoCardReadTransactionResponse> noCardReadRequest(PaymentRequest paymentRequest,
      String paymentId, ProviderAccount account) {
    NoCardReadTransactionRequest noCardReadTransactionRequest = threeCTransformer.populateNoCardReadRequest(
        paymentRequest, paymentId, account);
    log.info("NoCardReadRequest generated");
    try {
      String noCardReadTransaction = mapper.writeValueAsString(noCardReadTransactionRequest);
      log.debug("noCardReadTransactionRequest : {}", noCardReadTransaction);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return webClient
        .post()
        .uri(threeCProperties.getEndpoints().getTransactions())
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .body(BodyInserters.fromValue(noCardReadTransactionRequest))
        .retrieve()
        .onStatus(HttpStatusCode::isError, clientResponse -> Mono.error(
            new PaymentServiceException(HttpStatus.INTERNAL_SERVER_ERROR,
                String.format("Error encountered during moto transaction for payment with id %s.",
                    paymentId), ErrorCodes.UNABLE_TO_PARSE_PROVIDER_RESPONSE)))
        .bodyToMono(NoCardReadTransactionResponse.class);
  }

  public Mono<NoCardReadTransactionResponse> paypalMitRequest(
      PaypalForwardAPITransactionResponse paypalForwardAPITransactionResponse,
      PaymentRequest paymentRequest, String paymentId, ProviderAccount account) {

    if (paypalForwardAPITransactionResponse.getStatus() != 200) {
      throw new PaymentServiceException(HttpStatus.INTERNAL_SERVER_ERROR,
          String.format("Unable to proceed paypal MIT transaction [%s].",
              paypalForwardAPITransactionResponse.getStatus()), ErrorCodes.PROVIDER_ERROR);
    }
    PaypalForwardAPITransactionResponseBody paypalForwardAPITransactionResponseBody;
    NoCardReadTransactionRequest noCardReadTransactionRequest;
    try {
      ObjectMapper mapper = new ObjectMapper().configure(
          DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      paypalForwardAPITransactionResponseBody = mapper.readValue(
          paypalForwardAPITransactionResponse.getBody(),
          PaypalForwardAPITransactionResponseBody.class);
      log.debug(new ObjectAppendingMarker("paypalForwardAPITransactionResponse",
              paypalForwardAPITransactionResponseBody),
          "PayPal Forwarding API Transaction Response Body {}.",
          paypalForwardAPITransactionResponseBody);

    } catch (Exception ex) {
      throw new PaymentServiceException(HttpStatus.INTERNAL_SERVER_ERROR,
          String.format("Unable to proceed paypal MIT transaction [%s].",
              paypalForwardAPITransactionResponse.getStatus()),
          ErrorCodes.UNABLE_TO_PARSE_PROVIDER_RESPONSE);
    }

    if (Boolean.TRUE.equals(checkPaypalSuccessCodes(paypalForwardAPITransactionResponseBody))) {
      noCardReadTransactionRequest = threeCTransformer.populatePaypalMITRequest(
          paypalForwardAPITransactionResponseBody, paymentRequest, paymentId, account);
      log.debug(new ObjectAppendingMarker("paypalMitRequest", noCardReadTransactionRequest),
          "PayPal and Planet MIT request {}.", noCardReadTransactionRequest);
    } else {
      throw new PaymentServiceException(HttpStatus.INTERNAL_SERVER_ERROR,
          String.format("PayPal Forwarding API Transaction Declined/Refused [%s].",
              paypalForwardAPITransactionResponse.getStatus()), ErrorCodes.PAYPAL_REFUSED);
    }

    return webClient
        .post()
        .uri(threeCProperties.getEndpoints().getTransactions())
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .body(BodyInserters.fromValue(noCardReadTransactionRequest))
        .retrieve()
        .onStatus(HttpStatusCode::isError, clientResponse -> Mono.error(
            new PaymentServiceException(HttpStatus.INTERNAL_SERVER_ERROR, String.format(
                "Error encountered during paypal mit transaction for payment with id %s.",
                paymentId), ErrorCodes.UNABLE_TO_PARSE_PROVIDER_RESPONSE)))
        .bodyToMono(NoCardReadTransactionResponse.class);
  }

  @Timed(description = "time taken to create a token with 3C", value = "3c.tokenise")
  public Mono<CreateTokenResponse> createToken(CreateTokenRequest createTokenRequest) {
    return webClient
        .post().uri(threeCProperties.getEndpoints().getTokenCreate())
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(BodyInserters.fromFormData(
            threeCTransformer.populateCreateTokenFormData(createTokenRequest)))
        .retrieve()
        .onStatus(HttpStatusCode::isError, clientResponse -> Mono.error(
            new PaymentServiceException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error encountered during create token",
                ErrorCodes.UNABLE_TO_PARSE_PROVIDER_RESPONSE)))
        .bodyToMono(CreateTokenResponse.class);
  }

  @Timed(description = "time taken to update a token with 3C", value = "3c.tokenise")
  public Mono<UpdateTokenResponse> updateToken(UpdateTokenRequest updateTokenRequest) {
    return webClient
        .post().uri(threeCProperties.getEndpoints().getTokenUpdate())
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(BodyInserters.fromFormData(
            threeCTransformer.populateUpdateTokenFormData(updateTokenRequest)))
        .retrieve()
        .onStatus(HttpStatusCode::isError, clientResponse -> Mono.error(
            new PaymentServiceException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error encountered during update token",
                ErrorCodes.UNABLE_TO_PARSE_PROVIDER_RESPONSE)))
        .bodyToMono(UpdateTokenResponse.class);
  }

  @Timed(description = "time taken to refund a 3C transaction", value = "3c.transactional.refund")
  public Mono<ReverseByTransactionIdResponse> refund(PaymentsSchema paymentsSchema) {
    ReverseByTransactionIdRequest reverseByTransactionIdRequest = threeCTransformer.populateReverseByTransactionIdRequest(
        paymentsSchema);
    return webClient.post().uri(String.format(threeCProperties.getEndpoints().getRefund(),
            paymentsSchema.getProviderResponse().getProviderReference()))
        .contentType(APPLICATION_JSON)
        .body(BodyInserters.fromValue(reverseByTransactionIdRequest))
        .retrieve()
        .onStatus(HttpStatusCode::isError, clientResponse -> Mono.error(
            new PaymentServiceException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error encountered during transactional refund.", ErrorCodes.PROVIDER_ERROR)))
        .bodyToMono(ReverseByTransactionIdResponse.class);
  }

  @Timed(description = "time taken to refund a 3C transaction", value = "3c.transactional.refund")
  public Mono<StartReconciliationResponse> startReconciliation(String siteIdentifier,
      String paymentSubtype) {
    // get e-merchant details
    EMerchantDetails eMerchantDetails = eMerchantService.getEMerchantDetails(paymentSubtype,
        siteIdentifier);
    StartReconciliationRequest startReconciliationRequest = StartReconciliationRequest.builder()
        .validationId(eMerchantDetails.getUsername())
        .validationCode(eMerchantDetails.getPassword())
        .build();
    return webClient.post().uri(threeCProperties.getEndpoints().getReconciliation())
        .contentType(APPLICATION_JSON)
        .body(BodyInserters.fromValue(startReconciliationRequest))
        .retrieve()
        .onStatus(HttpStatusCode::isError, clientResponse -> Mono.error(
            new PaymentServiceException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error encountered triggering reconciliation for site.",
                ErrorCodes.PROVIDER_ERROR)))
        .bodyToMono(StartReconciliationResponse.class);
  }

  @Timed(description = "time taken to retrieve refund response from 3C", value = "3c.payment.refund")
  public Mono<NoCardReadTransactionResponse> noCardReadRequest(RefundRequest refundRequest,
      String requestId, ProviderAccount account) {
    NoCardReadTransactionRequest noCardReadTransactionRequest = threeCTransformer.populateNoCardReadRequest(
        refundRequest, account, requestId);
    return webClient
        .post()
        .uri(threeCProperties.getEndpoints().getTransactions())
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .body(BodyInserters.fromValue(noCardReadTransactionRequest))
        .retrieve()
        .onStatus(HttpStatusCode::isError, clientResponse -> Mono.error(
            new PaymentServiceException(HttpStatus.INTERNAL_SERVER_ERROR,
                String.format("Error encountered during refunding payment with request id %s.",
                    requestId), ErrorCodes.UNABLE_TO_PARSE_PROVIDER_RESPONSE)))
        .bodyToMono(NoCardReadTransactionResponse.class);

  }

  @Timed(description = "time taken to retrieve Payments schema from 3C", value = "3c.get.payment.fallback")
  public Mono<PaymentProviderTransactionResponse> retrievePaymentProviderTransactionResponse(
      PaymentsSchema paymentsSchema) {
    log.info("Retrieving payment from 3C with payment id {}", paymentsSchema.getPaymentId());
    PaymentProviderTransactionRequest paymentProviderTransactionRequest = threeCTransformer.populatePaymentProviderTransactionRequest(
        paymentsSchema);
    return webClient
        .post()
        .uri(threeCProperties.getEndpoints().getTransactions())
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .body(BodyInserters.fromValue(paymentProviderTransactionRequest))
        .retrieve()
        .onStatus(HttpStatusCode::isError, clientResponse -> Mono.empty())
        .bodyToMono(PaymentProviderTransactionResponse.class);
  }

  public Mono<PaypalForwardAPITransactionResponse> paypalRequest(PaymentRequest paymentRequest,
      String paymentId, ProviderAccount account) {

    PaypalForwardAPITransactionRequest paypalForwardAPITransactionRequest = threeCTransformer.populatePaypalRequest(
        paymentRequest, paymentId, account);

    CustomerRequest customerRequest = new CustomerRequest()
        .firstName(isNull(paymentRequest.getPayment().getBilling().getFirstName()) ? ""
            : paymentRequest.getPayment().getBilling().getFirstName())
        .lastName(isNull(paymentRequest.getPayment().getBilling().getLastName()) ? ""
            : paymentRequest.getPayment().getBilling().getLastName())
        .id(isNull(paymentId) ? "" : paymentId)
        .deviceData(isNull(paymentRequest.getPayment().getPaypalDeviceData()) ? ""
            : paymentRequest.getPayment().getPaypalDeviceData())
        .paymentMethodNonce(isNull(paymentRequest.getPayment().getPaypalNonce()) ? ""
            : paymentRequest.getPayment().getPaypalNonce());
    Result<Customer> customerResult = getBrainTreeGateway().customer()
        .create(isNull(customerRequest) ? null : customerRequest);
    if (customerResult.isSuccess()) {
      Customer customer = customerResult.getTarget();
      String customerToken = customer.getPaymentMethods().get(0).getToken();
      log.debug("paypalRequest: customerToken: {} successful", customerToken);
      paypalForwardAPITransactionRequest.setPaymentMethodToken(customerToken);
      paypalForwardAPITransactionRequest.setMerchantId(paypalConfig.getMerchantID());
      paypalForwardAPITransactionRequest.setDeviceData(
          paymentRequest.getPayment().getPaypalDeviceData());
      log.debug(new ObjectAppendingMarker("paypalForwardAPITransactionRequest",
              paypalForwardAPITransactionRequest), "PayPal Forwarding API Transaction Request {}.",
          paypalForwardAPITransactionRequest);
    } else {
      throw new PaymentServiceException(HttpStatus.INTERNAL_SERVER_ERROR, String.format(
          "paypal customer creation is unsuccessful: " + customerResult.getMessage() + ": %s",
          paymentId), ErrorCodes.PAYPAL_ERROR);
    }

    return webClient
        .post()
        .uri(paypalConfig.getPaypalTransactions())
        .headers(httpHeaders -> httpHeaders.setBasicAuth(paypalConfig.getPublicKey(),
            paypalConfig.getPrivateKey()))
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .body(BodyInserters.fromValue(paypalForwardAPITransactionRequest))
        .retrieve()
        .onStatus(httpStatus -> httpStatus.value() == 504,
            error -> Mono.error(new PaymentServiceException(HttpStatus.GATEWAY_TIMEOUT,
                String.format(
                    "Unable to handle create paypal payment request due to timed out with payment id %s",
                    paymentId),
                ErrorCodes.PAYPAL_TIMEOUT)))
        .onStatus(HttpStatusCode::isError, clientResponse ->
            Mono.error(new PaymentServiceException(HttpStatus.INTERNAL_SERVER_ERROR,
                String.format(
                    "Error encountered during MIT transaction for payment with payment id %s, clientResponse %s",
                    paymentId, clientResponse),
                ErrorCodes.UNABLE_TO_PARSE_PROVIDER_RESPONSE))
        )
        .bodyToMono(PaypalForwardAPITransactionResponse.class);
  }

  @Timed(description = "time taken to create a paypal token")
  public Mono<PaypalClientTokenResponse> createPaypalClientToken(String countryCode) {
    PaypalToken response = getBrainTreeGatewayClientToken(countryCode);
    if (response == null) {
      return Mono.error(new PaymentServiceException(HttpStatus.INTERNAL_SERVER_ERROR, String.format(
          "Error encountered during creating paypal client token with country code %s.",
          countryCode), ErrorCodes.UNABLE_TO_PARSE_PROVIDER_RESPONSE));
    }
    return Mono.just(PaypalClientTokenResponse.builder()
            .clientToken(response.getToken())
            .generatedAt(LocalDateTime.now())
            .clientId(response.getClientId())
            .build())
        .doOnError(clientResponse -> Mono.error(
            new PaymentServiceException(HttpStatus.INTERNAL_SERVER_ERROR, String.format(
                "Error encountered during creating paypal client token with country code %s.",
                countryCode), ErrorCodes.UNABLE_TO_PARSE_PROVIDER_RESPONSE)));
  }

  public PaypalToken getBrainTreeGatewayClientToken(String countryCode) {
    PaypalToken paypalToken = TOKENS.get(countryCode.toLowerCase());
    try {
      if (paypalToken == null) {
        return generateToken(countryCode.toLowerCase());
      }
      Instant tokenExpiryDateTime = paypalToken.getTokenExpiryDateTime();
      Instant tokenInstantNow = Instant.now();
      if (tokenInstantNow.isAfter(tokenExpiryDateTime)) {
        log.info(
            "getBrainTreeGatewayClientToken :: Paypal token is expired and current time: {} Token Expiry Time: {}",
            tokenInstantNow, tokenExpiryDateTime);
        paypalToken = generateToken(countryCode.toLowerCase());
      }
    } catch (Exception ex) {
      log.error("Error encountered during creating paypal client token", ex);
      paypalToken = null;
    }
    return paypalToken;
  }

  @Timed(description = "time taken to retrieve MIT_CC response from 3C", value = "3c.payment.mitcc")
  public Mono<NoCardReadTransactionResponse> mitCcRequest(PaymentRequest paymentRequest,
                                                          String paymentId,
                                                          ProviderAccount account) {

    NoCardReadTransactionRequest noCardReadTransactionRequest =
        threeCTransformer.populateMitCcRequest(paymentRequest, paymentId, account);

    return webClient
        .post()
        .uri(threeCProperties.getEndpoints().getTransactions())
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .body(BodyInserters.fromValue(noCardReadTransactionRequest))
        .retrieve()
        .onStatus(HttpStatusCode::isError, clientResponse -> Mono.error(
            new PaymentProcessingException(
                ErrorCode.DIGITAL_THREEC_MIT_CC_PAYMENT_EXCEPTION,
                String.format("Error encountered during MIT_CC transaction for payment with id %s.",
                    paymentId))))
        .bodyToMono(NoCardReadTransactionResponse.class);
  }


  private PaypalToken generateToken(String countryCode) {
    PaypalToken paypalToken;
    ClientTokenRequest clientTokenRequest = merchantAccountId(countryCode);
    if (clientTokenRequest.getMerchantAccountId() != null) {
      String inputToken = paypalTokenService.generateToken(getBrainTreeGateway(),
          clientTokenRequest);
      paypalToken = new PaypalToken(countryCode, inputToken);
      TOKENS.put(countryCode, paypalToken);
    } else {
      throw new PaymentServiceException(HttpStatus.INTERNAL_SERVER_ERROR,
          String.format(
              "Error encountered during creating paypal client token request with country code %s.",
              countryCode),
          ErrorCodes.UNABLE_TO_PARSE_PROVIDER_RESPONSE);
    }
    return paypalToken;
  }

  public BraintreeGateway getBrainTreeGateway() {
    return new BraintreeGateway(Environment.parseEnvironment(paypalConfig.getEnvironment()),
        paypalConfig.getMerchantID(), paypalConfig.getPublicKey(), paypalConfig.getPrivateKey());
  }

  public ClientTokenRequest merchantAccountId(String countryCode) {
    ClientTokenRequest clientTokenRequest = new ClientTokenRequest();
    List<PaypalAccountName> accountNames = paypalConfig.getAccountNames();
    clientTokenRequest.merchantAccountId(accountNames.stream()
        .filter(x -> x.getCountry().equalsIgnoreCase(countryCode)).map(PaypalAccountName::getValue)
        .findFirst().orElse(null));

    return clientTokenRequest;
  }

  public Boolean checkPaypalSuccessCodes(
      PaypalForwardAPITransactionResponseBody paypalForwardAPITransactionResponseBody) {
    var result =
        paypalForwardAPITransactionResponseBody.getResponse().getParams().getResult().isBlank() ? ""
            : paypalForwardAPITransactionResponseBody.getResponse().getParams().getResult();
    var trxState =
        paypalForwardAPITransactionResponseBody.getResponse().getParams().getTransactionState()
            .isBlank() ? "" : paypalForwardAPITransactionResponseBody.getResponse().getParams()
            .getTransactionState();
    var cardFraudInfo = paypalForwardAPITransactionResponseBody.getResponse().getParams()
        .getCardFraudInfo();
    String cardFraudInfoDecision = "";
    if (Objects.nonNull(cardFraudInfo)) {
      cardFraudInfoDecision = cardFraudInfo.getDecision().isBlank() ? ""
          : paypalForwardAPITransactionResponseBody.getResponse().getParams().getCardFraudInfo()
              .getDecision();
    }

    return paypalConfig.getSuccessCodes().getResult().equalsIgnoreCase(result) &&
        paypalConfig.getSuccessCodes().getTrxState().equalsIgnoreCase(trxState) &&
        (paypalConfig.getSuccessCodes().getFraudInfoDecision()
            .equalsIgnoreCase(cardFraudInfoDecision) ||
            cardFraudInfoDecision.isBlank());
  }
}