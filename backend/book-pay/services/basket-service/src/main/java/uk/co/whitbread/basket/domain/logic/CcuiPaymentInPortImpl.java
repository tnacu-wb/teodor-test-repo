package uk.co.whitbread.basket.domain.logic;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isAnyBlank;
import static uk.co.whitbread.basket.domain.logic.utils.BusinessAllowancesUtils.buildBasketBookingAllowances;
import static uk.co.whitbread.basket.domain.logic.utils.PaymentUtils.createConfirmationPaymentDetails;
import static uk.co.whitbread.basket.domain.logic.utils.PaymentUtils.validatePaymentResponse;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.AMENDING;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.COMPLETED;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.PROCESSING;
import static uk.co.whitbread.basket.domain.model.ccuieckoh.in.PaymentCcuiOption.ACCOUNT_COMPANY;
import static uk.co.whitbread.basket.domain.model.ccuieckoh.in.PaymentCcuiOption.NEW_CARD;
import static uk.co.whitbread.basket.domain.model.ccuieckoh.in.PaymentCcuiOption.NEW_PIBA;
import static uk.co.whitbread.basket.domain.model.ccuieckoh.in.PaymentCcuiOption.PAY_ON_ARRIVAL;
import static uk.co.whitbread.basket.domain.model.ccuieckoh.in.PaymentCcuiOption.RESERVE_WITHOUT_CARD;
import static uk.co.whitbread.basket.domain.model.payments.in.PaymentOption.PAY_NOW;
import static uk.co.whitbread.basket.domain.model.payments.out.ThreecPaymentStatus.FAILURE;
import static uk.co.whitbread.basket.domain.model.payments.out.ThreecPaymentStatus.NO_PAYMENT_ATTEMPT;
import static uk.co.whitbread.basket.infrastructure.rest.client.config.BasketConstants.PREPAID_CHANNEL_VALUE;
import static uk.co.whitbread.basket.infrastructure.rest.client.config.BasketConstants.SUBTYPE_MOTO;
import static uk.co.whitbread.basket.utils.SanitizingUtils.sanitize;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import uk.co.whitbread.basket.domain.exception.BasketReferenceNotValidException;
import uk.co.whitbread.basket.domain.exception.CompanyIdNotFoundException;
import uk.co.whitbread.basket.domain.exception.ErrorCode;
import uk.co.whitbread.basket.domain.exception.PaymentBusinessException;
import uk.co.whitbread.basket.domain.exception.PaymentException;
import uk.co.whitbread.basket.domain.logic.config.ThreecProp;
import uk.co.whitbread.basket.domain.logic.mapper.CcuiPaymentDomainMapper;
import uk.co.whitbread.basket.domain.logic.utils.BusinessAllowancesUtils;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.basket.out.BasketItem;
import uk.co.whitbread.basket.domain.model.basket.out.BasketStatus;
import uk.co.whitbread.basket.domain.model.basket.out.CleanUpTime;
import uk.co.whitbread.basket.domain.model.basket.out.Country;
import uk.co.whitbread.basket.domain.model.business.in.BusinessItems;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.AccountCompanyItems;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.CcuiExtraItems;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.CcuiPaymentRequest;
import uk.co.whitbread.basket.domain.model.ccuieckoh.out.PaymentCcuiResponse;
import uk.co.whitbread.basket.domain.model.content.out.HotelPaymentInformation;
import uk.co.whitbread.basket.domain.model.email.out.EmailNotificationEventType;
import uk.co.whitbread.basket.domain.model.feature.FeatureFlag;
import uk.co.whitbread.basket.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.basket.domain.model.ohip.in.UpdateCustomReferenceNumberRequest;
import uk.co.whitbread.basket.domain.model.payments.in.Amount;
import uk.co.whitbread.basket.domain.model.payments.in.Card;
import uk.co.whitbread.basket.domain.model.payments.in.DiscountRequest;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentOption;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentRequest;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentsConfirmation;
import uk.co.whitbread.basket.domain.model.payments.in.RoomType;
import uk.co.whitbread.basket.domain.model.payments.out.BasketPaymentStatus;
import uk.co.whitbread.basket.domain.model.payments.out.BasketRequestAction;
import uk.co.whitbread.basket.domain.model.payments.out.BookingConfirmationDetails;
import uk.co.whitbread.basket.domain.model.payments.out.EckohResponse;
import uk.co.whitbread.basket.domain.model.payments.out.PaymentResponse;
import uk.co.whitbread.basket.domain.model.payments.out.ThreecPaymentStatus;
import uk.co.whitbread.basket.domain.model.refund.in.Refund;
import uk.co.whitbread.basket.domain.model.refund.in.RefundReason;
import uk.co.whitbread.basket.domain.model.refund.in.RefundRequest;
import uk.co.whitbread.basket.domain.model.refund.in.RefundType;
import uk.co.whitbread.basket.domain.model.reservation.out.ReservationByBasketRefResponse;
import uk.co.whitbread.basket.domain.model.reservation.out.ReservationPackagesDetails;
import uk.co.whitbread.basket.domain.ports.primary.CcuiPaymentInPort;
import uk.co.whitbread.basket.domain.ports.secondary.BasketOhipOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.BasketOrderOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.CcuiPaymentOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.ContentOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.HotelReservationOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.PaymentOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.RefundOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.RulesAgentOutPort;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.shared.auth.account.Account;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@Slf4j
@RequiredArgsConstructor
public class CcuiPaymentInPortImpl implements CcuiPaymentInPort {

  private static final String UPDATE_TOKEN_REQUEST_ID_PREFIX = "PAY-NOW-UPDATE-TOKEN-";
  public static final String SUBTYPE_PIBAGB = "PIBAGB";
  public static final String SUBTYPE_PIBADE = "PIBADE";
  public static final String PIBA_TYPE = "PIBA";
  private static final String CARD_TYPE = "CARD";
  private static final String CHANNEL = "CCUI";
  private static final String COUNTRY_VALUE = "gb";
  private static final String LANGUAGE_VALUE = "en";
  private static final String GERMANY = "Germany";
  private static final String DEUTSCHLAND = "Deutschland";
  public static final String CONTACT_BANK_CODES = "contactBankCodes";
  public static final String INCORRECT_CARD_DETAILS_CODES = "incorrectCardDetailsCodes";
  public static final String TRY_AGAIN_CODES = "tryAgainCodes";
  private final Long basketValidity;
  private final BasketOrderOutPort basketOrderOutPort;
  private final HotelReservationOutPort reservationOutPort;
  private final PaymentOutPort paymentOutPort;
  private final BasketOutPort basketOutPort;
  private final CcuiPaymentOutPort ccuiPaymentOutPort;
  private final ContentOutPort contentOutPort;
  private final EmailNotificationService emailNotificationService;
  private final RefundOutPort refundOutPort;
  private final RulesAgentOutPort rulesAgentOutPort;
  private final ThreecProp threecProperties;
  private final BasketOhipOutPort basketOhipOutPort;
  private final CcuiPaymentDomainMapper ccuiPaymentDomainMapper;
  private final ConcurrentTracer concurrentTracer;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;
  private final AuthenticatedUserService authenticatedUserService;
  private final CleanUpTime cleanUpTime;

  public PaymentCcuiResponse initiateCcuiPaymentProcess(
      String basketReference,
      CcuiPaymentRequest ccuiPaymentRequest) {
    log.debug("Enter initiate ccui payment process with basketReference={}",
        sanitize(basketReference));

    var basket = basketOutPort.getBasketById(basketReference);
    validateBasket(basket, getNewPaymentOption(ccuiPaymentRequest));
    //for change payment flow
    var initialPaymentOption =  basket.getPaymentOption();
    updateBasketStatus(basket);

    var paymentOption = ccuiPaymentRequest.getPaymentOption();
    var subPaymentType = ccuiPaymentRequest.getSubPaymentType();

    var ccAgentId = unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCcuiAgentIdLog())
        && authenticatedUserService.isUserAuthenticated() ? getCcAgentId() : null;

    // Currently this is implemented only for ACCOUNT_COMPANY bookings
    // TODO: Enhance this to support amending PIBA and RESERVE_WITHOUT_CARD bookings as well
    if (basket.getOriginalBasketId() != null
        && ACCOUNT_COMPANY.name().equalsIgnoreCase(paymentOption)) {
      updateA2cFields(ccuiPaymentRequest, basket);

      PaymentsConfirmation paymentsConfirmation = PaymentsConfirmation
          .builder()
          .language(ccuiPaymentRequest.getPaymentRequest().getBooking().getLanguage().toUpperCase())
          .channel(ccuiPaymentRequest.getPaymentRequest().getBooking().getChannel())
          .paymentOptionSelected("PAY_ON_ARRIVAL")
          .emailAddress(ccuiPaymentRequest.getPaymentRequest().getPayment().getBilling().getEmail())
          .ccAgentId(ccAgentId)
          .build();

      basketOrderOutPort.processAmend(basket, paymentsConfirmation,
          BasketRequestAction.AMEND.getReqAction());

      return getResponse(basket.getBasketId(), basket.getReference(), AMENDING);
    }

    var reservations = reservationOutPort.getReservationsByBasketReference(basketReference,
        "false", true, ccuiPaymentRequest.getUseCache());

    var companyId = extractAndValidateCompanyId(ccuiPaymentRequest);

    if (ACCOUNT_COMPANY.name().equalsIgnoreCase(paymentOption)) {
      reservationOutPort.attachProfileToReservations(reservations.getHotelId(), companyId,
          basket.getItems().stream().map(BasketItem::getSourceId).collect(Collectors.toSet()));

      String profileId = Optional.of(ccuiPaymentRequest).map(CcuiPaymentRequest::getCcuiExtraItems)
              .map(CcuiExtraItems::getAccountCompanyItems)
              .map(AccountCompanyItems::getCompanyId)
              .orElse(null);
      final var hotelInfo = contentOutPort
              .getHotelPaymentDetails(basket.getHotelId(), COUNTRY_VALUE, LANGUAGE_VALUE);
      Country hotelCountry = getHotelCountry(hotelInfo);

      if (StringUtils.isNotBlank(profileId) && validateNegotiatedRates(profileId)
              && Country.DE.getCountryCode().equals(hotelCountry.getCountryCode())) {
        updateBillingAddressCcui(ccuiPaymentRequest, basket);
      }
    }

    //TODO - currency value will be fixed with AEM integration for getBusinessNotes call
    if (!RESERVE_WITHOUT_CARD.name().equalsIgnoreCase(paymentOption)
        && nonNull(ccuiPaymentRequest.getCcuiExtraItems())
        && nonNull(ccuiPaymentRequest.getCcuiExtraItems().getCardPresent())
        && !ccuiPaymentRequest.getCcuiExtraItems().getCardPresent()) {
      updateBusinessItems(basket, ccuiPaymentRequest.getPaymentRequest().getBooking().getLanguage(),
          reservations.getHotelId(), ccuiPaymentRequest.getCcuiExtraItems().getBusinessItems(), reservations,
          ccuiPaymentRequest.getPaymentRequest().getPayment().getType(),
          RESERVE_WITHOUT_CARD.name().equalsIgnoreCase(initialPaymentOption) ? null : companyId);
    }

    if (RESERVE_WITHOUT_CARD.name().equalsIgnoreCase(paymentOption) || ACCOUNT_COMPANY.name()
        .equalsIgnoreCase(paymentOption)) {
      Basket basketOrder =
              updateBasketIfReserveWoCardOrAccountCompanyPaymentOption(ccuiPaymentRequest, basket,
                      paymentOption);
      final var hotelInfo = contentOutPort
          .getHotelPaymentDetails(basket.getHotelId(), COUNTRY_VALUE, LANGUAGE_VALUE);
      Country hotelCountry = getHotelCountry(hotelInfo);
      if (ACCOUNT_COMPANY.name().equalsIgnoreCase(paymentOption)
          && Country.DE.getCountryCode().equals(hotelCountry.getCountryCode())) {
        updateBillingAddressCcui(ccuiPaymentRequest, basket);
      }
      if (RESERVE_WITHOUT_CARD.name().equalsIgnoreCase(paymentOption)
          && Country.DE.getCountryCode().equals(hotelCountry.getCountryCode())
          && validateIsDifferentBillingAddress(ccuiPaymentRequest)
          && validateAddressTpe(ccuiPaymentRequest)) {
        updateBillingAddressCcui(ccuiPaymentRequest, basket);
      }
      processBasket(basketOrder, initialPaymentOption, null, ccAgentId);

      return getResponse(basket.getBasketId(), basket.getReference(), PROCESSING);

    } else if (NEW_CARD.name().equalsIgnoreCase(paymentOption)
        || isNewPibaPaymentOption(ccuiPaymentRequest, paymentOption)) {

      validateEckoh(basket);

      final var eckohPaymentResponse =
          ccuiPaymentOutPort.getPaymentConfirmation(basket.getPaymentID());
      final var hotelInfo = contentOutPort
          .getHotelPaymentDetails(basket.getHotelId(), COUNTRY_VALUE, LANGUAGE_VALUE);
      Country hotelCountry = getHotelCountry(hotelInfo);
      if (Country.DE.getCountryCode().equals(hotelCountry.getCountryCode())
          && validateIsDifferentBillingAddress(ccuiPaymentRequest)
          && validateAddressTpe(ccuiPaymentRequest)) {
        updateBillingAddressCcui(ccuiPaymentRequest, basket);
      }
      var paymentRequest =
          ccuiPaymentOutPort.createPaymentRequest(ccuiPaymentRequest.getPaymentRequest());
      updatePaymentRequest(eckohPaymentResponse, paymentRequest, reservations, basket,
          subPaymentType);

      updateTokenWithCardDetails(eckohPaymentResponse, ccuiPaymentRequest);
      final var paymentResponse = paymentOutPort.createPayment(paymentRequest);
      log.info("Received payment response for basketReference: {} with paymentId: {}",
          sanitize(basketReference), paymentResponse.getPaymentId());
      updateBasketIfNewCardOrPibaPaymentOption(ccuiPaymentRequest, basket, paymentRequest,
          paymentResponse);
      updatePaymentResponseForOrder(paymentResponse);

      var paymentStatus = ThreecPaymentStatus.valueOf(paymentResponse.getPaymentStatus());
      if (NO_PAYMENT_ATTEMPT.equals(paymentStatus) || FAILURE.equals(paymentStatus)) {
        updateFailedBasket(basket, paymentRequest);

        if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getThreecpReturnCodesMapping())) {
          Optional.ofNullable(
                  paymentResponse.getProviderResponse().getThreecResponse().getProviderResult())
              .filter(StringUtils::isNotEmpty)
              .ifPresent(this::handleReturnCode);
        }
      }

      validatePaymentResponse(basketReference, paymentResponse);
      if (!isChangePaymentFlow(initialPaymentOption, getNewPaymentOption(ccuiPaymentRequest))
          && isExpired(basket)) {
        log.error("Basket is no longer valid basketReference={}", sanitize(basketReference));
        if (basket.getPaymentOption().equals(PaymentOption.PAY_NOW.toString())) {
          log.info("Basket invalid, reverting payment for paymentID={}",
              paymentResponse.getPaymentId());
          revertPayment(basket, paymentResponse);
        }
        basket.setStatus(BasketStatus.FAILED);
        basket.setCleanUpTime(cleanUpTime.getCleanUpTime(basket));
        basketOutPort.updateBasket(basket);
        var exception = new BasketReferenceNotValidException(
            ErrorCode.DIGITAL_CCUI_INVALID_BASKET_EXCEPTION,
            String.format("Basket is no longer valid: %s", basketReference));
        ExceptionLogger.log(log, exception);
        throw exception;
      }

      if (isFolioThree(paymentRequest, hotelCountry)) {
        setPrePaidBookingPayee(basket);
      }

      // DNRQ-39005 - 3C payment service uses CCC for Contact Centre and Opera uses CCUI
      paymentResponse.getBooking().setChannel(CHANNEL);
      // DNRQ-32587 - 3C payment service does not return language value for MOTO
      paymentResponse.getBooking()
          .setLanguage(ccuiPaymentRequest.getPaymentRequest().getBooking().getLanguage());
      var bookingConfirmationDetails = getConfirmationPaymentDetails(basket, paymentResponse);
      try {
        processBasket(basket, initialPaymentOption, bookingConfirmationDetails, ccAgentId);
      } catch (PaymentException exception) {
        log.error("Rollback started: exception was thrown while trying to process order basket={}",
            sanitize(basketReference), exception);
        if (PaymentOption.PAY_NOW.toString().equals(basket.getPaymentOption())) {
          log.info("Order couldn't be processed, reverting payment for paymentID={}",
              paymentResponse.getPaymentId());
          revertPayment(basket, paymentResponse);
        }
        throw exception;
      }

      return getResponse(basket.getBasketId(), basket.getReference(), PROCESSING);
    }
    var exception = new PaymentException(ErrorCode.DIGITAL_INVALID_PAYMENT_OPTION_EXCEPTION,
        String.format(
            "Error while processing payment for CCUI with basketReference=%s."
                + "The payment option %s is not valid for basket %s",
            basket.getReference(),
            paymentOption,
            basket.getReference()));
    ExceptionLogger.log(log, exception);
    throw exception;
  }

  private void updateFailedBasket(Basket basket, PaymentRequest paymentRequest) {
    basket.setPaymentID(null);
    basket.setStatus(BasketStatus.FAILED);
    basket.setCleanUpTime(cleanUpTime.getCleanUpTime(basket));
    basket.setPaymentOption(paymentRequest.getBooking().getType());
    basket.setTotalCost(null);
    basket.setEmailAddress(paymentRequest.getPayment().getBilling().getEmail());
    basket.setCurrency(null);
    basket.setPaymentChannel(paymentRequest.getBooking().getChannel());
    basketOutPort.updateBasket(basket);
  }

  private String getCcAgentId() {
    return authenticatedUserService.getCurrentUserAccount()
            .map(Account::getEmail)
            .orElse(null);
  }

  private void handleReturnCode(String returnCodeStr) {
    try {
      int returnCode = Integer.parseInt(returnCodeStr);
      log.warn("Planet issued return code: {}", returnCode);

      Map<String, ErrorCode> errorMapping = Map.of(
          CONTACT_BANK_CODES, ErrorCode.PAYMENT_CONTACT_BANK_EXCEPTION,
          INCORRECT_CARD_DETAILS_CODES, ErrorCode.PAYMENT_INCORRECT_CARD_EXCEPTION,
          TRY_AGAIN_CODES, ErrorCode.PAYMENT_TRY_AGAIN_EXCEPTION
      );

      for (var entry : errorMapping.entrySet()) {
        if (threecProperties.getReturnCodes().get(entry.getKey()).contains(returnCode)) {
          throw new PaymentBusinessException(entry.getValue(), returnCodeStr);
        }
      }

    } catch (NumberFormatException e) {
      log.error("Invalid return code format received from Planet: {}", returnCodeStr);
    }
  }

  private String extractAndValidateCompanyId(CcuiPaymentRequest ccuiPaymentRequest) {
    log.debug("Entering validate company id for payment option={}",
        ccuiPaymentRequest.getPaymentOption());
    if (ACCOUNT_COMPANY.name().equalsIgnoreCase(ccuiPaymentRequest.getPaymentOption())) {
      return Optional.of(ccuiPaymentRequest)
          .map(CcuiPaymentRequest::getCcuiExtraItems)
          .map(CcuiExtraItems::getAccountCompanyItems)
          .map(AccountCompanyItems::getCompanyId)
          .filter(StringUtils::isNotEmpty)
          .orElseThrow(
              () -> {
                var exception = new CompanyIdNotFoundException(
                    ErrorCode.DIGITAL_CCUI_INVALID_COMPANY_EXCEPTION,
                    String.format("Company id not found for payment option: %s",
                        ccuiPaymentRequest.getPaymentOption()));
                ExceptionLogger.log(log, exception);
                return exception;
              });
    }
    return null;
  }

  private void updateBasketStatus(Basket basket) {
    if (BasketStatus.FAILED.equals(basket.getStatus())) {
      log.info("Reattempt on failed transaction");
    }
    var status = basket.getOriginalBasketId() != null ? AMENDING : BasketStatus.PAY_PENDING;
    basket.setStatus(status);
    basketOutPort.updateBasket(basket);
  }

  private void updateTokenWithCardDetails(final PaymentResponse paymentResponse,
                                          final CcuiPaymentRequest paymentRequest) {
    if (PaymentOption.PAY_NOW.name().equals(paymentRequest.getPaymentRequest().getBooking().getType())) {
      log.debug("Entering create token update request with paymentResponse={}, "
          + "paymentRequest={}", paymentResponse, paymentRequest);
      var token = getToken(paymentResponse.getProviderResponse().getEckohResponse());
      var requestId = UPDATE_TOKEN_REQUEST_ID_PREFIX + token;
      var updateTokenRequest =
          ccuiPaymentDomainMapper.toUpdateTokenRequest(paymentRequest.getPaymentRequest().getPayment().getCard(), token,
              requestId);
      paymentOutPort.updateToken(updateTokenRequest);
    }
  }

  private void updateBasketIfNewCardOrPibaPaymentOption(
      CcuiPaymentRequest ccuiPaymentRequest,
      Basket basket, PaymentRequest paymentRequest, PaymentResponse paymentResponse) {
    log.debug("createPayment call with paymentResponse={}", paymentResponse);
    basket.setPaymentOption(paymentRequest.getBooking().getType());
    basket.setPaymentID(paymentResponse.getPaymentId());
    basket.setTotalCost(String.valueOf(paymentRequest.getPayment().getAmount().getMinorUnits()));
    basket.setCurrency(paymentRequest.getPayment().getAmount().getCurrency());
    basket.setEmailAddress(paymentRequest.getPayment().getBilling().getEmail());
    basket.setSendMail(ccuiPaymentRequest.getSendMail());
    basketOutPort.updateBasket(basket);
  }

  private static boolean isNewPibaPaymentOption(CcuiPaymentRequest ccuiPaymentRequest,
                                                String paymentOption) {
    return NEW_PIBA.name().equalsIgnoreCase(paymentOption)
        && isPibaSubPaymentType(ccuiPaymentRequest);
  }

  private static boolean isPibaSubPaymentType(CcuiPaymentRequest ccuiPaymentRequest) {
    if (nonNull(ccuiPaymentRequest.getSubPaymentType())) {
      return SUBTYPE_PIBAGB.equalsIgnoreCase(ccuiPaymentRequest.getSubPaymentType())
          || SUBTYPE_PIBADE.equalsIgnoreCase(ccuiPaymentRequest.getSubPaymentType());
    }
    return false;
  }

  private void updateBusinessItems(Basket basket, String language, String hotelId, BusinessItems businessItems,
      ReservationByBasketRefResponse reservationByBasketRefResponse, String cardType, String companyId) {
    var completableFutures =
        new ArrayList<CompletableFuture<Void>>(reservationByBasketRefResponse.getReservationByIdList().size());

    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getSaveAllowancesInBasket())) {
      completableFutures.add(CompletableFuture.runAsync(concurrentTracer.wrap(() -> {
        basket.setBookingAllowances(buildBasketBookingAllowances(businessItems.getBusinessAllowances()));
        basketOutPort.updateBasket(basket);
      })));
    }

    final var businessAllowanceRules = rulesAgentOutPort.getBusinessAllowanceRules().getBusinessAllowances();
    final var businessNotesResponse = contentOutPort.getBusinessNotes(language);

    completableFutures.addAll(reservationByBasketRefResponse.getReservationByIdList().stream()
        .map(reservation -> CompletableFuture.runAsync(concurrentTracer.wrap(() -> {
          final var packageCodes = reservation.getReservationPackageList().stream()
              .map(ReservationPackagesDetails::getPackageCode)
              .toList();
          final var finalBusinessItems =
              BusinessAllowancesUtils.getBusinessItems(businessItems, businessAllowanceRules, businessNotesResponse,
                  packageCodes, cardType);
          reservationOutPort.updateBusinessItems(finalBusinessItems, List.of(reservation.getReservationId()),
              hotelId, companyId, CHANNEL);
        }))).toList());

    completableFutures.forEach(CompletableFuture::join);
  }

  private void revertPayment(final Basket basket, final PaymentResponse paymentResponse) {
    basket.setPaymentStatus(BasketPaymentStatus.REFUNDING);
    uk.co.whitbread.basket.domain.model.refund.in.Amount refundAmount =
        uk.co.whitbread.basket.domain.model.refund.in.Amount.builder()
            .currency(paymentResponse.getPayment().getAmount().getCurrency())
            .minorUnits(paymentResponse.getPayment().getAmount().getMinorUnits())
            .build();
    RefundRequest refundRequest = RefundRequest.builder()
        .refundType(RefundType.FULL)
        .hotelCode(basket.getHotelId())
        .refund(Refund
            .builder()
            .amount(refundAmount)
            .reason(RefundReason.ROLLBACK)
            .build())
        .build();
    refundOutPort.processRefund(basket.getBasketId(), refundRequest,
        paymentResponse.getPaymentId());
    emailNotificationService.sendEmailNotificationEvent(basket, EmailNotificationEventType.FAIL,
        basket.getEmailAddress(), false);
  }

  private Basket updateBasketIfReserveWoCardOrAccountCompanyPaymentOption(
      CcuiPaymentRequest ccuiPaymentRequest, Basket basket, String paymentOption) {
    basket.setPaymentOption(paymentOption);
    basket.setCcuiExtraItems(ccuiPaymentRequest.getCcuiExtraItems());
    basket.setSendMail(ccuiPaymentRequest.getSendMail());
    basket.setEmailAddress(
        ccuiPaymentRequest.getPaymentRequest().getPayment().getBilling().getEmail());
    return basketOutPort.updateBasket(basket);
  }

  private void updateA2cFields(CcuiPaymentRequest request, Basket basket) {
    var completableFutures =
        new ArrayList<CompletableFuture<Void>>();

    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getSaveAllowancesInBasket())) {
      completableFutures.add(CompletableFuture.runAsync(concurrentTracer.wrap(() -> {
        updateA2cBasketFields(request, basket);
      })));
    }

    // TODO: updateBusinessItems should be used to include allowances also (routing instructions)
    completableFutures.add(CompletableFuture.runAsync(concurrentTracer.wrap(() -> {
      updateA2cReservationFields(request, basket);
    })));

    completableFutures.forEach(CompletableFuture::join);
  }

  private void updateA2cReservationFields(CcuiPaymentRequest request, Basket basket) {
    var optionalCustomReferenceNumber = Optional.ofNullable(request.getCcuiExtraItems())
        .map(CcuiExtraItems::getBusinessItems)
        .map(BusinessItems::getCustomReferenceNumber);

    optionalCustomReferenceNumber.ifPresent(customReferenceNumber ->
        basketOhipOutPort.updateCustomReferenceNumber(
            buildCustomReferenceNumberRequest(basket, customReferenceNumber))
    );
  }

  private static UpdateCustomReferenceNumberRequest buildCustomReferenceNumberRequest(Basket basket,
      String customReferenceNumber) {
    return UpdateCustomReferenceNumberRequest
        .builder()
        .customReferenceNumber(customReferenceNumber)
        .hotelId(basket.getHotelId())
        .reservationIds(
            basket.getItems().stream().map(BasketItem::getSourceId).collect(Collectors.toSet()))
        .build();
  }

  private Basket updateA2cBasketFields(CcuiPaymentRequest request, Basket basket) {
    return Optional.ofNullable(request.getCcuiExtraItems().getBusinessItems())
        .map(BusinessItems::getBusinessAllowances)
        .map(allowances -> {
          basket.setBookingAllowances(
              buildBasketBookingAllowances(request.getCcuiExtraItems().getBusinessItems()
                  .getBusinessAllowances()));
          return basketOutPort.updateBasket(basket);
        })
        .orElse(basket);
  }

  private void validateEckoh(Basket basket) {
    log.debug("Entering validate Eckoh for basketReference={}", basket.getReference());
    if (basket.getPaymentID() == null) {
      var exception = new PaymentException(ErrorCode.DIGITAL_ECKOH_NOT_LAUCHED_EXCEPTION,
          String.format(
              "Error while validate Eckoh the paymentId is null for basketReference=%s."
                  + "The Eckoh Iframe has not been launched!",
              basket.getReference()));
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }

  private void updatePaymentResponseForOrder(PaymentResponse paymentResponse) {
    log.debug("Entering update payment response for oder with paymentResponse={}", paymentResponse);
    var expiry = paymentResponse.getProviderResponse().getThreecResponse().getExpiry();
    paymentResponse.getProviderResponse().getThreecResponse()
        .setExpiry(expiry.substring(0, 2) + "/" + expiry.substring(2, 4));
  }

  private void validateBasket(Basket basket, String newPaymentOption) {
    log.debug("Entering validate basket with basketReference={}", basket.getReference());
    if (COMPLETED.equals(basket.getStatus()) && !isChangePaymentFlow(basket.getPaymentOption(), newPaymentOption)) {
      var message = String.format(
          "Error while validating basket the status is COMPLETED for basketReference=%s",
          basket.getBasketId());
      var exception = new BasketReferenceNotValidException(
          ErrorCode.DIGITAL_CCUI_INVALID_COMPLETED_BASKET_EXCEPTION, message);
      ExceptionLogger.log(log, exception);
      throw exception;
    }

    if (isAnyBlank(basket.getHotelId(), basket.getBasketId())) {
      var exception = new PaymentException(ErrorCode.DIGITAL_MISSING_BASKET_REF_OR_HOTEL_ID_EXCEPTION,
          String.format(
              "Error while validating basket, basketReference or hotelId are empty, %s",
              basket.getReference()));
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }

  private void updatePaymentRequest(
      PaymentResponse paymentResponse, PaymentRequest paymentRequest,
      ReservationByBasketRefResponse reservations, Basket basket, String subPaymentType) {
    log.debug("Entering update payment request for paymentRequest={} and basket={}", paymentRequest,
        basket);

    if (SUBTYPE_PIBAGB.equalsIgnoreCase(subPaymentType)) {
      paymentRequest.getPayment().setType(PIBA_TYPE);
    } else {
      paymentRequest.getPayment().setType(CARD_TYPE);
    }
    if (!reservations.getReservationByIdList().isEmpty()) {
      paymentRequest.getBooking().setArrivalDate(
          reservations.getReservationByIdList().get(0).getRoomStay().getArrivalDate());
      paymentRequest.getBooking().setDepartureDate(
          reservations.getReservationByIdList().get(0).getRoomStay().getDepartureDate());
    }
    var eckohResponse = paymentResponse.getProviderResponse().getEckohResponse();
    paymentRequest.getPayment().setSubType(SUBTYPE_MOTO);
    paymentRequest.getPayment().setCard(Card.builder().token(getToken(eckohResponse))
        .expiryMonth(getExpiryFrom(eckohResponse, 0, 2))
        .expiryYear(getExpiryFrom(eckohResponse, 2, 4)).build());
    paymentRequest.getPayment().setAmount(Amount.builder().currency(reservations.getCurrencyCode())
        .minorUnits(getTotalCost(reservations, basket.getReference())).build());
    paymentRequest.getBooking().setRooms(reservations.getReservationByIdList().stream().map(
            resInfo -> new RoomType(resInfo.getRoomStay().getRoomType(),
                resInfo.getRoomStay().getRatePlanCode(), resInfo.getRoomStay().getAdultsNumber()))
        .toList());
    paymentRequest.getBooking().setReference(basket.getBasketId());
    paymentRequest.getBooking().setBookingReference(basket.getReference());
  }

  private static String getToken(EckohResponse eckohResponse) {
    return eckohResponse == null ? null
        : eckohResponse.getToken();
  }

  private static String getExpiryFrom(EckohResponse eckohResponse, int beginIndex, int endIndex) {
    if (eckohResponse != null && eckohResponse.getExpiry() != null
        && eckohResponse.getExpiry().length() >= endIndex) {
      return eckohResponse.getExpiry().substring(beginIndex, endIndex);
    }
    return null;
  }

  @Override
  public void updateDiscount(DiscountRequest discountRequest) {
    log.debug("Entering update discount request for basketReference={}",
        discountRequest.getBasketReference());
    var basket = basketOutPort.getBasketById(discountRequest.getBasketReference());
    validateBasket(basket, null);
    var reservations = reservationOutPort.getReservationsByBasketReference(basket.getBasketId(),
        "false", false);
    reservationOutPort.updateDiscount(discountRequest, getReservationIds(basket),
        reservations.getHotelId(), reservations.getCurrencyCode());
  }

  private void processBasket(Basket basket, String initialPaymentOption,
      BookingConfirmationDetails bookingConfirmationDetails, String ccAgentId) {
    log.debug("Entering process basket with basketReference={}", sanitize(basket.getReference()));
    var reqAction = BasketRequestAction.COMMIT.getReqAction();
    if (isChangePaymentFlow(initialPaymentOption, basket.getPaymentOption())) {

      reqAction = BasketRequestAction.CHANGE_PAY.getReqAction();
    }
    basketOrderOutPort.processOrder(basket, reqAction, bookingConfirmationDetails, ccAgentId);
    basketOutPort.updateBasketStatus(basket.getBasketId(), BasketStatus.PROCESSING, Optional.empty());
  }

  private BigDecimal getTotalCost(
      ReservationByBasketRefResponse reservations,
      String basketReference) {
    final var cost = reservations.getTotalCost() != null ? reservations.getTotalCost()
        : reservations.getBalanceOutstanding();
    if (cost == null) {
      var exception = new PaymentException(ErrorCode.DIGITAL_CCUI_PAYMENT_EXCEPTION,
          String.format("Total reservation costs missing for basket %s", basketReference));
      ExceptionLogger.log(log, exception);
      throw exception;
    }
    return cost.multiply(BigDecimal.valueOf(100));
  }

  private List<String> getReservationIds(Basket basket) {
    List<BasketItem> basketItems = basket.getItems();

    if (CollectionUtils.isEmpty(basketItems)) {
      return Collections.emptyList();
    }

    var reservationIds = Optional.of(basketItems).map(
        item -> item.stream().map(BasketItem::getSourceId).collect(Collectors.toSet()).stream()
            .toList()).orElse(Collections.emptyList());
    reservationIds.forEach(resId -> log.trace("Extracted reservationId={} from basket", resId));
    return reservationIds;
  }

  private PaymentCcuiResponse getResponse(String basketReference, String bookingReference,
      BasketStatus basketStatus) {
    return PaymentCcuiResponse.builder()
        .reference(basketReference)
        .bookingReference(bookingReference)
        .status(basketStatus)
        .build();
  }

  private BookingConfirmationDetails getConfirmationPaymentDetails(
      Basket basket,
      PaymentResponse paymentResponse) {
    HotelPaymentInformation hotelPaymentInformation =
        contentOutPort.getHotelPaymentDetails(basket.getHotelId(),
            Country.valueOf(paymentResponse.getBooking().getLanguage().toUpperCase())
                .getCountryCode(), paymentResponse.getBooking().getLanguage().toLowerCase());
    return createConfirmationPaymentDetails(basket.getChannel(), hotelPaymentInformation,
        paymentResponse, unleashWrapper, null);
  }

  private boolean isExpired(Basket basket) {
    final long now = Instant.now().truncatedTo(ChronoUnit.SECONDS).toEpochMilli();
    final long createdAt = Instant.parse(basket.getCreatedAt()).toEpochMilli();
    return now - createdAt > basketValidity;
  }

  private void updateBillingAddressCcui(CcuiPaymentRequest ccuiPaymentRequest, Basket basket) {

    List<String> reservationIds = getReservationIds(basket);
    basketOhipOutPort.updateReservationBillingAddressCcui(ccuiPaymentRequest, reservationIds);
    log.debug("Billing address updated successfully for {}", basket.getBasketId());
  }

  private Country getHotelCountry(final HotelPaymentInformation hotelInfo) {
    return switch (hotelInfo.getAddress().getCountry()) {
      case GERMANY, DEUTSCHLAND -> Country.DE;
      default -> Country.EN;
    };
  }

  private boolean validateAddressTpe(CcuiPaymentRequest ccuiPaymentRequest) {
    return nonNull(ccuiPaymentRequest.getPaymentRequest().getPayment().getCard())
        && nonNull(ccuiPaymentRequest.getPaymentRequest().getPayment().getCard().getCardHolderAddress())
        && StringUtils.isNotBlank(
        ccuiPaymentRequest.getPaymentRequest().getPayment().getCard().getCardHolderAddress().getAddressType());
  }

  private boolean validateIsDifferentBillingAddress(CcuiPaymentRequest ccuiPaymentRequest) {
    return nonNull(ccuiPaymentRequest.getPaymentRequest().getPayment().getBilling())
        && ccuiPaymentRequest.getPaymentRequest().getPayment().getBilling().isDifferentBillingAddress();
  }

  private String getNewPaymentOption(CcuiPaymentRequest ccuiPaymentRequest) {
    if (RESERVE_WITHOUT_CARD.name().equals(ccuiPaymentRequest.getPaymentOption())
        || ACCOUNT_COMPANY.name().equals(ccuiPaymentRequest.getPaymentOption())) {
      return ccuiPaymentRequest.getPaymentOption();
    } else {
      return ccuiPaymentRequest.getPaymentRequest().getBooking().getType();
    }
  }

  private boolean isChangePaymentFlow(String initialPaymentOption, String newPaymentOption) {
    return RESERVE_WITHOUT_CARD.name().equalsIgnoreCase(initialPaymentOption)
        && (ACCOUNT_COMPANY.name().equalsIgnoreCase(newPaymentOption)
        || PAY_ON_ARRIVAL.name().equalsIgnoreCase(newPaymentOption));
  }

  private boolean validateNegotiatedRates(String profileId) {
    return basketOhipOutPort.getNegotiatedRates(profileId) != null;
  }

  private boolean validatePrePaidBooking(PaymentRequest paymentRequest) {
    return null != paymentRequest.getPayment().getBilling()
            && paymentRequest.getPayment().getBilling().isBookerIsNotGuest();
  }

  private void setPrePaidBookingPayee(Basket basket) {
    reservationOutPort.updateBusinessItems(null, getReservationIds(basket).stream().toList(),
            basket.getHotelId(), null, PREPAID_CHANNEL_VALUE);
  }

  private boolean isFolioThree(PaymentRequest paymentRequest, Country hotelCountry) {
    return PAY_NOW.name().equals(paymentRequest.getBooking().getType())
            && isFolio3FeatureFlagEnabled()
            && isHotelInGermany(hotelCountry)
            && validatePrePaidBooking(paymentRequest);
  }

  private boolean isFolio3FeatureFlagEnabled() {
    return unleashWrapper.isEnabled(unleashWrapper.featureFlag().getSavePaymentInstructionFolioThree());
  }

  private boolean isHotelInGermany(Country hotelCountry) {
    return Country.DE.getCountryCode().equals(hotelCountry.getCountryCode());
  }

}