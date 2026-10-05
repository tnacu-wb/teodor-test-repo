package uk.co.whitbread.basket.domain.logic;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isAnyBlank;
import static uk.co.whitbread.basket.domain.logic.utils.BusinessAllowancesUtils.buildBasketBookingAllowances;
import static uk.co.whitbread.basket.domain.logic.utils.BusinessAllowancesUtils.buildBusinessItemsFromBusinessAccount;
import static uk.co.whitbread.basket.domain.logic.utils.PaymentUtils.createConfirmationPaymentDetails;
import static uk.co.whitbread.basket.domain.logic.utils.PaymentUtils.createDistributionConfirmationPaymentDetails;
import static uk.co.whitbread.basket.domain.logic.utils.PaymentUtils.validatePaymentResponse;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.AMENDED;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.AMENDING;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.AMEND_FAILED;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.CIOL_FAILED;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.COMPLETED;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.FAILED;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.PAY_PENDING;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.PROCESSING;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.SECURE_FAILED;
import static uk.co.whitbread.basket.domain.model.payments.in.PaymentOption.PAY_NOW;
import static uk.co.whitbread.basket.domain.model.payments.in.PaymentOption.PAY_ON_ARRIVAL;
import static uk.co.whitbread.basket.domain.model.payments.in.PaymentOption.RESERVE_WITHOUT_CARD;
import static uk.co.whitbread.basket.domain.model.payments.out.ThreecPaymentStatus.FAILURE;
import static uk.co.whitbread.basket.domain.model.payments.out.ThreecPaymentStatus.NO_PAYMENT_ATTEMPT;
import static uk.co.whitbread.basket.domain.model.payments.out.ThreecPaymentStatus.SUCCESS;
import static uk.co.whitbread.basket.infrastructure.rest.client.config.BasketConstants.BB_CHANNEL;
import static uk.co.whitbread.basket.infrastructure.rest.client.config.BasketConstants.COUNTRY_CODE_DE;
import static uk.co.whitbread.basket.infrastructure.rest.client.config.BasketConstants.COUNTRY_CODE_DE_SHORT;
import static uk.co.whitbread.basket.infrastructure.rest.client.config.BasketConstants.DISTRIBUTION_CHANNEL;
import static uk.co.whitbread.basket.infrastructure.rest.client.config.BasketConstants.PREPAID_CHANNEL_VALUE;
import static uk.co.whitbread.basket.utils.SanitizingUtils.sanitize;

import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import uk.co.whitbread.basket.domain.exception.BasketReferenceNotValidException;
import uk.co.whitbread.basket.domain.exception.ErrorCode;
import uk.co.whitbread.basket.domain.exception.PaymentException;
import uk.co.whitbread.basket.domain.exception.PaymentOptionNotValidException;
import uk.co.whitbread.basket.domain.logic.config.DistributionProperties;
import uk.co.whitbread.basket.domain.logic.config.ThreecProp;
import uk.co.whitbread.basket.domain.logic.mapper.CompanyAddressMapper;
import uk.co.whitbread.basket.domain.logic.mapper.PaymentResponseWebhookMapper;
import uk.co.whitbread.basket.domain.logic.utils.BusinessAllowancesUtils;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.basket.out.BasketError;
import uk.co.whitbread.basket.domain.model.basket.out.BasketErrorType;
import uk.co.whitbread.basket.domain.model.basket.out.BasketItem;
import uk.co.whitbread.basket.domain.model.basket.out.BasketStatus;
import uk.co.whitbread.basket.domain.model.basket.out.CleanUpTime;
import uk.co.whitbread.basket.domain.model.basket.out.Country;
import uk.co.whitbread.basket.domain.model.business.in.BusinessItems;
import uk.co.whitbread.basket.domain.model.cdh.CdhSearchCompaniesRequest;
import uk.co.whitbread.basket.domain.model.content.out.HotelPaymentInformation;
import uk.co.whitbread.basket.domain.model.email.out.EmailNotificationEventType;
import uk.co.whitbread.basket.domain.model.feature.FeatureFlag;
import uk.co.whitbread.basket.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.basket.domain.model.payments.in.Address;
import uk.co.whitbread.basket.domain.model.payments.in.Amount;
import uk.co.whitbread.basket.domain.model.payments.in.Billing;
import uk.co.whitbread.basket.domain.model.payments.in.Booking;
import uk.co.whitbread.basket.domain.model.payments.in.Card;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentOption;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentRequest;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentsConfirmation;
import uk.co.whitbread.basket.domain.model.payments.in.RoomType;
import uk.co.whitbread.basket.domain.model.payments.out.BasketPaymentStatus;
import uk.co.whitbread.basket.domain.model.payments.out.BasketRequestAction;
import uk.co.whitbread.basket.domain.model.payments.out.BookingConfirmationDetails;
import uk.co.whitbread.basket.domain.model.payments.out.CdhSearchCompaniesResponse;
import uk.co.whitbread.basket.domain.model.payments.out.CompanyAddress;
import uk.co.whitbread.basket.domain.model.payments.out.InitiatePaymentResponse;
import uk.co.whitbread.basket.domain.model.payments.out.PaymentRequiredDetails;
import uk.co.whitbread.basket.domain.model.payments.out.PaymentResponse;
import uk.co.whitbread.basket.domain.model.payments.out.PaymentStatus;
import uk.co.whitbread.basket.domain.model.refund.in.Refund;
import uk.co.whitbread.basket.domain.model.refund.in.RefundReason;
import uk.co.whitbread.basket.domain.model.refund.in.RefundRequest;
import uk.co.whitbread.basket.domain.model.refund.in.RefundType;
import uk.co.whitbread.basket.domain.model.reservation.out.RateInfo;
import uk.co.whitbread.basket.domain.model.reservation.out.RateInfoSummary;
import uk.co.whitbread.basket.domain.model.reservation.out.Reservation;
import uk.co.whitbread.basket.domain.model.reservation.out.ReservationByBasketRefResponse;
import uk.co.whitbread.basket.domain.model.reservation.out.ReservationPackagesDetails;
import uk.co.whitbread.basket.domain.ports.primary.PaymentInPort;
import uk.co.whitbread.basket.domain.ports.secondary.BasketOhipOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.BasketOrderOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.CdhSearchCompaniesOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.ContentOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.HotelReservationOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.PaymentOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.RefundOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.RulesAgentOutPort;
import uk.co.whitbread.basket.generated.models.payments.PaymentDto.SubTypeEnum;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@SuppressWarnings("checkstyle:SummaryJavadoc")
@Slf4j
@AllArgsConstructor
public class PaymentInPortImpl implements PaymentInPort {

  private static final String PAYPAL_BOOKING_CONFIRMATION_CODE = "DPP";
  private static final String COUNTRY_VALUE = "gb";
  private static final String LANGUAGE_VALUE = "en";
  public static final String BUSINESS = "BUSINESS";
  public static final String CONTACT_BANK_CODES = "contactBankCodes";
  public static final String INCORRECT_CARD_DETAILS_CODES = "incorrectCardDetailsCodes";
  public static final String TRY_AGAIN_CODES = "tryAgainCodes";
  public static final String PAYMENT_CONTACT_BANK_EXCEPTION_VALUE_DESCRIPTION = "PAYMENT_CONTACT_BANK_EXCEPTION_VALUE";
  public static final String PAYMENT_INCORRECT_CARD_EXCEPTION_VALUE_DESCRIPTION =
      "PAYMENT_INCORRECT_CARD_EXCEPTION_VALUE";
  public static final String PAYMENT_TRY_AGAIN_EXCEPTION_VALUE_DESCRIPTION = "PAYMENT_TRY_AGAIN_EXCEPTION_VALUE";
  private final Long basketValidity;
  private final BasketOrderOutPort basketOrderOutPort;
  private final HotelReservationOutPort reservationOutPort;
  private final PaymentOutPort paymentOutPort;
  private final BasketOutPort basketOutPort;
  private final ContentOutPort contentOutPort;
  private final EmailNotificationService emailNotificationService;
  private final RefundOutPort refundOutPort;
  private final RulesAgentOutPort rulesAgentOutPort;
  private final DistributionProperties distributionProperties;
  private final ThreecProp threecProperties;
  private final PaymentResponseWebhookMapper paymentResponseWebhookMapper;
  private final ConcurrentTracer concurrentTracer;
  private final BasketOhipOutPort basketOhipOutPort;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;
  private final CdhSearchCompaniesOutPort cdhSearchCompaniesOutPort;
  private final AuthenticatedUserService authenticatedUserService;
  private final CompanyAddressMapper companyAddressMapper;
  private final CleanUpTime cleanUpTime;

  @Override
  public InitiatePaymentResponse initiatePaymentProcess(final String basketReference,
      final PaymentRequest paymentRequest) {
    log.info("initiatePaymentProcess(): Received paymentRequest for basketReference ={}", sanitize(basketReference));

    if (StringUtils.isNotBlank(paymentRequest.getTmpBasketRef())) {
      var paymentResponse = handleInitPaymentAmend(paymentRequest);
      return InitiatePaymentResponse.builder()
          .status(PaymentStatus.PAYMENT_REQUIRED)
          .paymentRequiredDetails(
              PaymentRequiredDetails.builder()
                  .paymentRedirect(
                      paymentResponse.getProviderResponse().getThreecResponse().getIPageHtml())
                  .sessionId(
                      paymentResponse.getProviderResponse().getThreecResponse().getSessionId())
                  .template(paymentResponse.getProviderResponse().getThreecResponse().getTemplate())
                  .providerUrl(paymentResponse.getProviderResponse().getThreecResponse().getProviderUrl())
                  .build())
          .build();
    }

    final var billing = paymentRequest.getPayment().getBilling();
    final var bookingChannel = paymentRequest.getBooking().getChannel();
    var basket = basketOutPort.getBasketById(basketReference);
    basket.setPaymentOption(paymentRequest.getBooking().getType());
    basket.setEmailAddress(billing.getEmail());
    basket.setPaymentChannel(bookingChannel);

    var basketStatusCompletedStatus = basket.getOriginalBasketId() != null ? AMENDED : COMPLETED;
    if (!paymentRequest.isCiol() && !paymentRequest.isSecureBooking()
        && (basketStatusCompletedStatus.equals(basket.getStatus()) || isExpired(basket))) {
      var message = String.format("Basket is no longer valid basketReference=%s", basketReference);
      var exception = new BasketReferenceNotValidException(
          ErrorCode.DIGITAL_INVALID_BASKET_EXCEPTION, message);
      ExceptionLogger.log(log, exception);
      throw exception;
    }

    if (isAnyBlank(basket.getHotelId(), basket.getReference())) {
      log.error("The basket is not valid (hotelId/reference are missing!");
      var exception = new BasketReferenceNotValidException(ErrorCode.DIGITAL_INVALID_BASKETREF_EXCEPTION,
          "The basket is not valid (hotelId/reference are missing!");
      ExceptionLogger.log(log, exception);
      throw exception;
    }
    //CIOL PAYMENT VALIDATION
    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCheckInOnline()) && paymentRequest.isCiol()) {
      preventCiolCharge(basket, paymentRequest, basketReference);
    }
    // CompanyQuestionAndAnswerDetails will be updated if provided.
    updateCompanyQuestionAndAnswerDetails(paymentRequest,
        new HashSet<>(getReservationIds(basket)), basket.getHotelId());
    boolean pibaCardNotPresent = paymentRequest.getPayment().getPibaCardPresent() != null
        && !paymentRequest.getPayment().getPibaCardPresent();
    
    boolean useCachedBooking = paymentRequest.isUseCache();
    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCheckInOnline())
        && paymentRequest.isCiol()) {
      basket.setIsCheckInOnlinePay(true);
      basketOutPort.updateBasket(basket);
      log.info("Initiate CIOL payment for basketReference={}, and disable caching", sanitize(basketReference));
      useCachedBooking = false;
    }

    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getSaveSecureBooking())
        && paymentRequest.isSecureBooking()) {
      basket.setIsSecureBooking(true);
      basketOutPort.updateBasket(basket);
      paymentRequest.getPayment().setSubType(SubTypeEnum.SECURE_BOOKING.getValue());
      log.info("Initiated Secure Booking payment for basketReference={}", basketReference);
    }

    if (DISTRIBUTION_CHANNEL.equals(bookingChannel)) {

      final var reservationByBasketRefResponse =
          reservationOutPort.getReservationsByBasketReference(basket.getBasketId(), Boolean.FALSE.toString(), false);

      if (pibaCardNotPresent) {
        var businessItems = buildBusinessItemsFromBusinessAccount(
            paymentRequest.getBusinessAccount(),
            distributionProperties.getMeals());
        updateBusinessItems(basket, paymentRequest.getBooking(), basket.getHotelId(), businessItems,
            reservationByBasketRefResponse, paymentRequest.getPayment().getType());
      } else {
        setDistributionPayee(basket);
      }

      if (CollectionUtils.isNotEmpty(paymentRequest.getSpecialRequests())
          || CollectionUtils.isNotEmpty(paymentRequest.getBookingNotes())) {
        reservationOutPort.updateSpecialRequests(paymentRequest.getSpecialRequests(),
            paymentRequest.getBookingNotes(),
            getReservationIds(basket).stream().toList(), basket.getHotelId());
      }

      var confirmationPaymentDetails = getDistributionConfirmationPaymentDetails(basket,
          paymentRequest);
      Optional.ofNullable(paymentRequest.getPayment().getSca())
          .ifPresent(sca -> confirmationPaymentDetails.setCitId(sca.getXid()));
      log.info("ConfirmationPaymentDetails={}", confirmationPaymentDetails);
      basketOutPort.updateBasketPayment(basket);
      processBasket(basketReference, basket, confirmationPaymentDetails);
      return InitiatePaymentResponse.builder()
          .status(PaymentStatus.NOT_REQUIRED)
          .build();
    }

    if (RESERVE_WITHOUT_CARD.name().equals(paymentRequest.getBooking().getType())) {
      var basketStatusProcessing = basket.getOriginalBasketId() != null ? AMENDING : PROCESSING;
      basket.setPaymentID(null);
      basket.setTotalCost(null);
      basket.setCurrency(null);
      basket.setStatus(basketStatusProcessing);
      basketOutPort.updateBasketPayment(basket);
      // To add BILLING address
      final var hotelInfo = contentOutPort.getHotelPaymentDetails(basket.getHotelId(), COUNTRY_VALUE, LANGUAGE_VALUE);
      Country hotelCountry = getHotelCountry(hotelInfo);
      if (Objects.nonNull(billing.getAddress())
              && StringUtils.isNotBlank(billing.getAddress().getAddressType())
              && Country.DE.getCountryCode().equals(hotelCountry.getCountryCode())
              && billing.isDifferentBillingAddress()) {

        updateBillingAddress(paymentRequest, basket, true, true, true);
      }

      basketOrderOutPort.processOrder(basket, BasketRequestAction.COMMIT.getReqAction(),
          null, null);
      return InitiatePaymentResponse.builder()
          .status(PaymentStatus.NOT_REQUIRED)
          .build();
    } else if (PAY_NOW.name().equals(paymentRequest.getBooking().getType())
        || PAY_ON_ARRIVAL.name().equals(paymentRequest.getBooking().getType())) {

      final var reservationByBasketRefResponse =
          reservationOutPort.getReservationsByBasketReference(basket.getBasketId(),
              Boolean.FALSE.toString(), true, useCachedBooking);

      if (pibaCardNotPresent) {
        updateBusinessItems(basket, paymentRequest.getBooking(), basket.getHotelId(),
            paymentRequest.getPayment().getBusinessItems(), reservationByBasketRefResponse,
            paymentRequest.getPayment().getType());
      }
      // To add BILLING address
      final var hotelInfo = contentOutPort.getHotelPaymentDetails(basket.getHotelId(), COUNTRY_VALUE, LANGUAGE_VALUE);
      Country hotelCountry = getHotelCountry(hotelInfo);

      if (PAY_NOW.name().equals(paymentRequest.getBooking().getType())
          && isSaveInFolio3(bookingChannel)
          && Country.DE.getCountryCode().equals(hotelCountry.getCountryCode())
          && validatePrePaidBooking(paymentRequest)) {
        setPrePaidBookingPayee(basket);
      }

      var paymentResponse = send3cPayment(basket, paymentRequest, reservationByBasketRefResponse);

      if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCaptureBillingAddressBb())
          && Objects.equals(BB_CHANNEL, bookingChannel)
          && Country.DE.getCountryCode().equals(hotelCountry.getCountryCode())) {

        if (Objects.isNull(billing.getCardBillingAddress())) {
          // updating billing address when NEW CARD payment option is selected
          CompanyAddress cdhCompanyAddress = getCdhCompanyAddress(bookingChannel);
          log.info("cdhCompanyAddress = {}, request address = {}", cdhCompanyAddress, billing.getAddress());
          updateBillingAddress(paymentRequest, basket,
              shouldUpdateCompanyProfile(cdhCompanyAddress, paymentRequest), true, !billing.isBookerIsNotGuest());
          Address requestAddress = paymentRequest.getPayment().getBilling().getAddress();
          if (isSameAddress(cdhCompanyAddress, requestAddress)
              && Objects.equals(BUSINESS, requestAddress.getAddressType())) {
            billing.setAddress(companyAddressMapper.toAddress(cdhCompanyAddress));
            updateBillingAddress(paymentRequest, basket, true, false, false);
          }
        } else if (!billing.isDifferentBillingAddress()) {
          // updating billing address when CENTRALLY/STORED card payment option is selected
          billing.setAddress(billing.getCardBillingAddress());
          CompanyAddress cdhCompanyAddress = getCdhCompanyAddress(bookingChannel);
          log.info("cdhCompanyAddress = {}, request cardBillingAddress = {}", cdhCompanyAddress, billing.getAddress());
          updateBillingAddress(paymentRequest, basket,
              shouldUpdateCompanyProfile(cdhCompanyAddress, paymentRequest), true, !billing.isBookerIsNotGuest());
        } else {
          defaultUpdateBillingAddress(paymentRequest, billing, basket, hotelCountry);
        }
      } else {
        defaultUpdateBillingAddress(paymentRequest, billing, basket, hotelCountry);
      }

      return InitiatePaymentResponse.builder()
          .status(PaymentStatus.PAYMENT_REQUIRED)
          .paymentRequiredDetails(
              PaymentRequiredDetails.builder()
                  .paymentRedirect(
                      paymentResponse.getProviderResponse().getThreecResponse().getIPageHtml())
                  .sessionId(
                      paymentResponse.getProviderResponse().getThreecResponse().getSessionId())
                  .template(paymentResponse.getProviderResponse().getThreecResponse().getTemplate())
                  .providerUrl(paymentResponse.getProviderResponse().getThreecResponse().getProviderUrl())
                  .build())
          .build();
    }

    var exception = new PaymentOptionNotValidException(ErrorCode.DIGITAL_INVALID_PAYMENT_OPT_EXCEPTION,
        String.format("The payment option %s is not valid for basket %s",
            paymentRequest.getBooking().getType(), basketReference));
    ExceptionLogger.log(log, exception);
    throw exception;
  }

  private void defaultUpdateBillingAddress(PaymentRequest paymentRequest, Billing billing, Basket basket,
      Country hotelCountry) {
    if (Objects.nonNull(billing.getAddress())
            && StringUtils.isNotBlank(billing.getAddress().getAddressType())
            && billing.isDifferentBillingAddress()
            && Country.DE.getCountryCode().equals(hotelCountry.getCountryCode())) {

      updateBillingAddress(paymentRequest, basket, true, true, true);
    }
  }

  private boolean isSaveInFolio3(@NotEmpty String channel) {
    var nonBbSaveInFolio3 =
        unleashWrapper.isEnabled(unleashWrapper.featureFlag().getSavePaymentInstructionFolioThree())
            && !Objects.equals(BB_CHANNEL, channel);
    var bbSaveInFolio3 =
        unleashWrapper.isEnabled(unleashWrapper.featureFlag().getSavePaymentInstructionFolioThreeBb())
            && Objects.equals(BB_CHANNEL, channel);
    return bbSaveInFolio3 || nonBbSaveInFolio3;
  }

  private boolean shouldUpdateCompanyProfile(CompanyAddress cdhCompanyAddress, PaymentRequest paymentRequest) {
    Address requestAddress = paymentRequest.getPayment().getBilling().getAddress();
    var addressType = requestAddress.getAddressType();

    return BUSINESS.equals(addressType) && !isSameAddress(cdhCompanyAddress, requestAddress);
  }

  private boolean isSameAddress(CompanyAddress cdhCompanyAddress, Address address) {
    if (!isValidAddress(cdhCompanyAddress)) {
      return false;
    }

    return Objects.equals(cdhCompanyAddress.getAddressLine1(), address.getLine1())
            && Objects.equals(cdhCompanyAddress.getPostCode(), address.getPostalCode())
            && isSameCountryCode(cdhCompanyAddress.getCountryCode(), address.getCountryCode());
  }

  private boolean isSameCountryCode(String cdhCountryCode, String countryCode) {
    log.debug("cdhCountryCode = {}, countryCode = {}", cdhCountryCode, countryCode);

    return StringUtils.equalsIgnoreCase(cdhCountryCode, countryCode)
            || (Objects.equals(cdhCountryCode, COUNTRY_CODE_DE_SHORT) && Objects.equals(countryCode, COUNTRY_CODE_DE))
            || (Objects.equals(cdhCountryCode, COUNTRY_CODE_DE) && Objects.equals(countryCode, COUNTRY_CODE_DE_SHORT));
  }

  private boolean isValidAddress(CompanyAddress cdhCompanyAddress) {
    return cdhCompanyAddress.getAddressLine1() != null
            && cdhCompanyAddress.getPostCode() != null
            && cdhCompanyAddress.getCountryCode() != null;
  }

  private CompanyAddress getCdhCompanyAddress(String bookingChannel) {
    String globalCompanyId =
            authenticatedUserService.getAuthenticatedUser().getAccount().getBartId();
    String email = authenticatedUserService.getAuthenticatedUser().getAccount().getEmail();

    CdhSearchCompaniesRequest searchCompaniesRequest =
            CdhSearchCompaniesRequest.builder()
                    .globalCompanyId(Integer.valueOf(globalCompanyId.trim()))
                    .accessedBy(email)
                    .accessContext(bookingChannel)
                    .build();

    CdhSearchCompaniesResponse searchCompaniesResponse =
            cdhSearchCompaniesOutPort.searchCompaniesFromCdh(searchCompaniesRequest);

    return searchCompaniesResponse.getResults().get(0).getCompanyAddress();
  }

  private void updateBillingAddress(PaymentRequest paymentRequest,
                                      Basket basket,
                                      boolean updateCompanyProfile,
                                      boolean updateContactProfile,
                                      boolean updateGuestProfile) {
    List<String> reservationIds = getReservationIds(basket);
    basketOhipOutPort.updateReservationBillingAddress(paymentRequest, reservationIds,
            updateGuestProfile, updateCompanyProfile, updateContactProfile);
    log.debug("Billing address updated successfully for {}", basket.getBasketId());
  }

  private Country getHotelCountry(final HotelPaymentInformation hotelInfo) {
    return switch (hotelInfo.getAddress().getCountry()) {
      case "Germany", "Deutschland" -> Country.DE;
      default -> Country.EN;
    };
  }

  @Override
  public InitiatePaymentResponse initiatePaypalPaymentProcess(final String basketReference,
      final PaymentRequest paymentRequest) {
    //Section 1 - Preliminary Logic before calling the Payment service
    var basket = basketOutPort.getBasketById(basketReference);
    basket.setPaymentChannel(paymentRequest.getBooking().getChannel());
    basket.setPaymentOption(paymentRequest.getBooking().getType());
    Billing billing = paymentRequest.getPayment().getBilling();
    basket.setEmailAddress(billing.getEmail());

    var basketStatusCompletedStatus = basket.getOriginalBasketId() != null ? AMENDED : COMPLETED;
    if (!paymentRequest.isCiol() && !paymentRequest.isSecureBooking()
        && (basketStatusCompletedStatus.equals(basket.getStatus()) || isExpired(basket))) {
      var message = String.format("Paypal - Basket is no longer valid basketReference=%s",
          basketReference);
      var exception = new BasketReferenceNotValidException(ErrorCode.DIGITAL_INVALID_BASKET_PAYPAL_EXCEPTION,
          message);
      ExceptionLogger.log(log, exception);
      throw exception;
    }

    if (isAnyBlank(basket.getHotelId(), basket.getReference())) {
      var exception = new BasketReferenceNotValidException(
          ErrorCode.DIGITAL_INVALID_BASKET_REF_PAYPAL_EXCEPTION,
          "Paypal - The basket is not valid (hotelId/reference are missing!");
      ExceptionLogger.log(log, exception);
      throw exception;
    }

    boolean useCachedBooking = paymentRequest.isUseCache();

    // To add BILLING address
    final var hotelInfo = contentOutPort.getHotelPaymentDetails(basket.getHotelId(), COUNTRY_VALUE, LANGUAGE_VALUE);
    Country hotelCountry = getHotelCountry(hotelInfo);
    if (null != billing.getAddress() && StringUtils
        .isNotBlank(billing.getAddress().getAddressType())
        && Country.DE.getCountryCode().equals(hotelCountry.getCountryCode())
        && billing.isDifferentBillingAddress()) {
      updateBillingAddress(paymentRequest, basket, true, true, true);
    }

    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCheckInOnline())
        && paymentRequest.isCiol()) {
      basket.setIsCheckInOnlinePay(true);
      basketOutPort.updateBasket(basket);
      log.info("Initiate CIOL payment for basketReference={}, and disable caching", sanitize(basketReference));
      useCachedBooking = false;
    }

    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getSaveSecureBooking())
        && paymentRequest.isSecureBooking()) {
      basket.setIsSecureBooking(true);
      basketOutPort.updateBasket(basket);
    }

    final var reservationByBasketRefResponse =
        reservationOutPort.getReservationsByBasketReference(basket.getBasketId(),
            Boolean.FALSE.toString(), true, useCachedBooking);

    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getSavePaymentInstructionFolioThree())
        && validatePrePaidBooking(paymentRequest)
        && Country.DE.getCountryCode().equals(hotelCountry.getCountryCode())) {
      setPrePaidBookingPayee(basket);
    }

    //Section 2 - Calling 3C to invoke PayPal
    var paymentResponse = send3cPayment(basket, paymentRequest, reservationByBasketRefResponse);

    //Section 3 - Post Payment Basket Updates
    validatePaymentResponse(basketReference, paymentResponse);
    updateCompanyQuestionAndAnswerDetails(paymentRequest,
        new HashSet<>(getReservationIds(basket)), basket.getHotelId());
    var confirmationPaymentDetails = getConfirmationPaymentDetails(basket, paymentResponse);
    confirmationPaymentDetails.setPaymentMethod(PAYPAL_BOOKING_CONFIRMATION_CODE);
    try {
      processBasket(basketReference, basket, confirmationPaymentDetails);
    } catch (PaymentException paymentException) {
      log.error(
          "Paypal - Rollback started: exception was thrown while trying to process order basket={}",
          sanitize(basketReference), paymentException);
      if (basket.getPaymentOption().equals(PaymentOption.PAY_NOW.toString())) {
        log.info("Order couldn't be processed, reverting payment for Paypal paymentID={}",
            paymentResponse.getPaymentId());
        revertPayment(basket, paymentResponse);
        CompletableFuture<Void> future = CompletableFuture.runAsync(concurrentTracer.wrap(() ->
            reservationOutPort.deleteRoutingInstructions(basket.getHotelId(),
                new HashSet<>(getReservationIds(basket)))
        ));
        future.join();
      }
      throw paymentException;
    }

    //Section 4 - Returning Non-Iframe response
    return InitiatePaymentResponse.builder()
        .status(PaymentStatus.NOT_REQUIRED)
        .build();
  }

  private void updateCompanyQuestionAndAnswerDetails(final PaymentRequest paymentRequest,
      Set<String> reservationIds,
      final String hotelId) {
    if (paymentRequest.getCompanyQuestionAndAnswerDetails() != null
        && (paymentRequest.getCompanyQuestionAndAnswerDetails().getPurchaseOrderQuestionAndAnswer()
        != null
        ||
        paymentRequest.getCompanyQuestionAndAnswerDetails().getCustomerReferenceQuestionAndAnswer()
            != null
        || CollectionUtils.isNotEmpty(paymentRequest
        .getCompanyQuestionAndAnswerDetails().getUserDefinedQuestionAndAnswers()))) {
      reservationOutPort.updateCompanyQuestionAndAnswerDetails(
          paymentRequest.getCompanyQuestionAndAnswerDetails(),
          reservationIds, hotelId);
    }
  }

  private PaymentResponse send3cPayment(Basket basket, PaymentRequest paymentRequest,
      ReservationByBasketRefResponse reservationByBasketRefResponse) {

    paymentRequest.getPayment().setAmount(Amount.builder()
        .currency(reservationByBasketRefResponse.getCurrencyCode())
        .minorUnits(totalCost(reservationByBasketRefResponse, basket))
        .build());
    paymentRequest.getBooking()
        .setRooms(reservationByBasketRefResponse.getReservationByIdList().stream()
            .map(resInfo -> new RoomType(resInfo.getRoomStay().getRoomType(),
                resInfo.getRoomStay().getRatePlanCode(), resInfo.getRoomStay().getAdultsNumber()))
            .toList());
    paymentRequest.getBooking().setReference(basket.getBasketId());
    paymentRequest.getBooking().setBookingReference(basket.getReference());
    PaymentResponse paymentResponse = paymentOutPort.createPayment(paymentRequest);
    basket.setPaymentID(paymentResponse.getPaymentId());
    basket.setTotalCost(String.valueOf(paymentRequest.getPayment().getAmount().getMinorUnits()));
    basket.setCurrency(paymentRequest.getPayment().getAmount().getCurrency());
    basket.setStatus(BasketStatus.PAY_PENDING);
    basketOutPort.updateBasketPayment(basket);
    return paymentResponse;
  }

  private void setDistributionPayee(Basket basket) {
    reservationOutPort.updateBusinessItems(null, getReservationIds(basket).stream().toList(),
        basket.getHotelId(), null, DISTRIBUTION_CHANNEL);
  }

  private void setPrePaidBookingPayee(Basket basket) {
    reservationOutPort.updateBusinessItems(null, getReservationIds(basket).stream().toList(),
        basket.getHotelId(), null, PREPAID_CHANNEL_VALUE);
  }

  private void updateBusinessItems(Basket basket, Booking booking, String hotelId, BusinessItems businessItems,
      ReservationByBasketRefResponse reservationByBasketRefResponse, String cardType) {
    var completableFutures =
        new ArrayList<CompletableFuture<Void>>(reservationByBasketRefResponse.getReservationByIdList().size());

    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getSaveAllowancesInBasket())) {
      completableFutures.add(CompletableFuture.runAsync(concurrentTracer.wrap(() -> {
        basket.setBookingAllowances(buildBasketBookingAllowances(businessItems.getBusinessAllowances()));
        basketOutPort.updateBasket(basket);
      })));
    }

    final var businessAllowanceRules = rulesAgentOutPort.getBusinessAllowanceRules()
        .getBusinessAllowances();
    final var businessNotesResponse = contentOutPort.getBusinessNotes(booking.getLanguage());

    completableFutures.addAll(reservationByBasketRefResponse.getReservationByIdList().stream()
        .map(reservation -> CompletableFuture.runAsync(concurrentTracer.wrap(() -> {
          final var packageCodes = reservation.getReservationPackageList().stream()
              .map(ReservationPackagesDetails::getPackageCode)
              .toList();
          final var finalBusinessItems =
              BusinessAllowancesUtils
                  .getBusinessItems(businessItems, businessAllowanceRules, businessNotesResponse,
                      packageCodes, cardType);
          reservationOutPort
              .updateBusinessItems(finalBusinessItems, List.of(reservation.getReservationId()),
                  hotelId, reservationByBasketRefResponse.getCompanyId(), booking.getChannel());
        }))).toList());

    completableFutures.forEach(CompletableFuture::join);
  }

  @Override
  public PaymentResponse paymentWebhook(final String basketReference,
      PaymentsConfirmation paymentsConfirmation) {
    var basket = basketOutPort.getBasketById(basketReference);
    var basketStatusFailed = basket.getOriginalBasketId() != null ? AMEND_FAILED : FAILED;
    var shouldCheckBasketValidity = true;

    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCheckInOnline())
        && Boolean.TRUE.equals(basket.getIsCheckInOnlinePay())) {
      basketStatusFailed = CIOL_FAILED;
      shouldCheckBasketValidity = false;
    }
    var paymentResponse = paymentResponseWebhookMapper
        .toPaymentsResponseModel(paymentsConfirmation);
    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getSaveSecureBooking())
        && Boolean.TRUE.equals(basket.getIsSecureBooking())) {
      if (FAILURE.toString().equals(paymentResponse.getPaymentStatus())) {
        basketStatusFailed = SECURE_FAILED;
      }
      shouldCheckBasketValidity = false;
    }
    if (SUCCESS.toString().equals(paymentResponse.getPaymentStatus())
        && basketStatusFailed.equals(basket.getStatus())) {
      log.error(
          "Order couldn't be processed as the basket is already failed, reverting payment for paymentID={}",
          paymentsConfirmation.getPaymentId());
      revertPayment(basket, paymentResponse);
    }
    if (PAY_PENDING.equals(basket.getStatus())) {
      if (NO_PAYMENT_ATTEMPT.toString().equals(paymentResponse.getPaymentStatus())
          || FAILURE.toString().equals(paymentResponse.getPaymentStatus())) {
        final CompletableFuture<Void> future = CompletableFuture.runAsync(concurrentTracer.wrap(() ->
            reservationOutPort.deleteRoutingInstructions(basket.getHotelId(),
                new HashSet<>(getReservationIds(basket)))
        ));

        Optional.ofNullable(paymentsConfirmation.getPaymentError())
            .ifPresentOrElse(error -> basket.setBasketError(
                new BasketError(error.getDescription(), error.getCode(), BasketErrorType.PAYMENT)),
                () -> {
                  var fraudDecision = Optional.ofNullable(
                      paymentResponse.getProviderResponse().getThreecResponse()
                          .getFraudCheckDecision())
                      .orElse("");
                  if (!fraudDecision.equals("ACCEPT")) {
                    basket.setBasketError(
                        new BasketError("A fraud check was triggered and no payment has been made",
                            ErrorCode.Constants.FRAUD_CHECK_FAILED, BasketErrorType.PAYMENT));
                  } else {
                    basket.setBasketError(
                        new BasketError("Card was declined and no payment attempt has been made",
                            ErrorCode.Constants.ERRORS_PAYMENT_GENERIC, BasketErrorType.PAYMENT));
                  }
                });

        if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getThreecpReturnCodesMapping())) {
          Optional.ofNullable(paymentsConfirmation.getReturnCode())
              .filter(StringUtils::isNotEmpty)
              .ifPresent(returnCode -> handleReturnCode(basket, returnCode));
        }


        updateFailedBasket(basket, basketStatusFailed);
        future.join();
      }

      validatePaymentResponse(basketReference, paymentResponse);
      if (shouldCheckBasketValidity) {
        checkBasketExpirationDate(basketReference, paymentsConfirmation, basket, paymentResponse,
            basketStatusFailed);
      }
      try {
        if (basket.getOriginalBasketId() != null) {
          paymentsConfirmation.setPaymentOptionSelected(PAY_NOW.toString());
          processAmend(basket, paymentsConfirmation);
        } else {
          var confirmationPaymentDetails = getConfirmationPaymentDetails(basket, paymentResponse);
          String threeDSIndicator = paymentResponse.getProviderResponse().getThreecResponse().getThreeDSIndicator();
          basket.setThreeDSIndicator(threeDSIndicator != null ? threeDSIndicator : EMPTY);
          processBasket(basketReference, basket, confirmationPaymentDetails);
        }
      } catch (Exception processingException) {
        log.error("Rollback started: exception was thrown while trying to process order basket={}",
            sanitize(basketReference), processingException);
        if (basket.getPaymentOption().equals(PaymentOption.PAY_NOW.toString())) {
          log.info("Order couldn't be processed, reverting payment for paymentID={}",
              paymentsConfirmation.getPaymentId());
          revertPayment(basket, paymentResponse);
        }
        throw processingException;
      }
    }
    return paymentResponse;
  }

  private void checkBasketExpirationDate(String basketReference,
      PaymentsConfirmation paymentsConfirmation,
      Basket basket, PaymentResponse paymentResponse, BasketStatus basketStatusFailed) {
    if (isExpired(basket)) {
      log.error("Basket is no longer valid basketReference={}", sanitize(basketReference));
      if (basket.getPaymentOption().equals(PaymentOption.PAY_NOW.toString())) {
        log.info("Basket invalid, reverting payment for paymentID={}",
            paymentsConfirmation.getPaymentId());
        revertPayment(basket, paymentResponse);
      }
      basket.setStatus(basketStatusFailed);
      basket.setCleanUpTime(cleanUpTime.getCleanUpTime(basket));
      basketOutPort.updateBasket(basket);
      var exception = new BasketReferenceNotValidException(
          ErrorCode.DIGITAL_NO_LONGER_BASKET_EXCEPTION,
          String.format("Basket is no longer valid: %s", basketReference));
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }

  private void handleReturnCode(Basket basket, String returnCodeStr) {
    try {
      int returnCode = Integer.parseInt(returnCodeStr);
      log.warn("Planet issued return code: {}", returnCode);
      String errorDescription = null;

      if (threecProperties.getReturnCodes().get(CONTACT_BANK_CODES).contains(returnCode)) {
        errorDescription = PAYMENT_CONTACT_BANK_EXCEPTION_VALUE_DESCRIPTION;
      } else if (threecProperties.getReturnCodes().get(INCORRECT_CARD_DETAILS_CODES).contains(returnCode)) {
        errorDescription = PAYMENT_INCORRECT_CARD_EXCEPTION_VALUE_DESCRIPTION;
      } else if (threecProperties.getReturnCodes().get(TRY_AGAIN_CODES).contains(returnCode)) {
        errorDescription = PAYMENT_TRY_AGAIN_EXCEPTION_VALUE_DESCRIPTION;
      }

      if (errorDescription != null) {
        basket.setBasketError(new BasketError(returnCodeStr, errorDescription, BasketErrorType.PAYMENT));
      }
    } catch (NumberFormatException e) {
      log.error("Invalid return code format received from Planet: {}", returnCodeStr);
    }
  }

  private void revertPayment(final Basket basket, final PaymentResponse paymentResponse) {
    basket.setPaymentStatus(BasketPaymentStatus.REFUNDING);
    var refund = Refund.builder()
        .reason(RefundReason.ROLLBACK)
        .build();
    Optional.ofNullable(paymentResponse.getPayment().getAmount()).ifPresent(
        a -> {
          var currency = paymentResponse.getPayment().getAmount().getCurrency();
          var minorUnits = paymentResponse.getPayment().getAmount().getMinorUnits();
          var amount = uk.co.whitbread.basket.domain.model.refund.in.Amount.builder()
              .currency(currency)
              .minorUnits(minorUnits).build();
          refund.setAmount(amount);
        });
    RefundRequest refundRequest = RefundRequest.builder()
        .refundType(RefundType.FULL)
        .hotelCode(basket.getHotelId())
        .refund(refund)
        .build();
    refundOutPort.processRefund(basket.getBasketId(), refundRequest,
        paymentResponse.getPaymentId());
    emailNotificationService.sendEmailNotificationEvent(basket, EmailNotificationEventType.FAIL,
        basket.getEmailAddress(), false);
  }

  private void processBasket(String basketReference, Basket basket,
      BookingConfirmationDetails bookingConfirmationDetails) {
    basketOrderOutPort.processOrder(basket, BasketRequestAction.COMMIT.getReqAction(),
        bookingConfirmationDetails, null);
    var newBasketStatus = basket.getOriginalBasketId() != null ? AMENDING : PROCESSING;
    basketOutPort.updateBasketStatus(basketReference, newBasketStatus, Optional.empty());
  }

  public void initiateProcessAmend(final String basketReference,
      PaymentsConfirmation processAmendRequest) {
    var basket = basketOutPort.getBasketById(basketReference);
    processAmend(basket, processAmendRequest);
  }

  private void processAmend(Basket basket, PaymentsConfirmation paymentsConfirmation) {
    basketOrderOutPort
        .processAmend(basket, paymentsConfirmation, BasketRequestAction.AMEND.getReqAction());
    basketOutPort.updateBasketStatus(basket.getBasketId(), BasketStatus.AMENDING, Optional.empty());
  }

  private PaymentResponse handleInitPaymentAmend(PaymentRequest paymentRequest) {
    var tmpBasket = basketOutPort.getBasketById(paymentRequest.getTmpBasketRef());
    var reservations = reservationOutPort.getReservationsByBasketReference(tmpBasket.getOriginalBasketId(),
        Boolean.FALSE.toString(), false, paymentRequest.isUseCache());
    var paymentCard = reservations.getReservationByIdList().get(0).getPaymentCard();

    if (StringUtils.isBlank(paymentCard.getToken())) {
      var exception = new PaymentException(ErrorCode.DIGITAL_PAYMENT_EXCEPTION,
          "Token must not be empty or null for amend flow!");
      ExceptionLogger.log(log, exception);
      throw exception;
    }
    var originalCard = Card
        .builder()
        .cardholderName(paymentCard.getCardHolderName())
        .cardType(paymentCard.getCardType())
        .cnpRequired(false)
        .token(paymentCard.getToken())
        .expiryMonth(Optional.ofNullable(paymentCard.getExpirationDate()).isPresent()
            ? String.valueOf(LocalDate.parse(paymentCard.getExpirationDate()).getMonth()) : "")
        .expiryYear(Optional.ofNullable(paymentCard.getExpirationDate()).isPresent()
            ? String.valueOf(LocalDate.parse(paymentCard.getExpirationDate()).getYear()) : "")
        .build();
    paymentRequest.getPayment().setCard(originalCard);

    PaymentResponse paymentResponse = paymentOutPort.createPayment(paymentRequest);

    tmpBasket.setPaymentOption(paymentRequest.getBooking().getType());
    tmpBasket.setEmailAddress(paymentRequest.getPayment().getBilling().getEmail());
    tmpBasket.setPaymentChannel(paymentRequest.getBooking().getChannel());
    tmpBasket.setPaymentID(paymentResponse.getPaymentId());
    tmpBasket.setTotalCost(String.valueOf(paymentRequest.getPayment().getAmount().getMinorUnits()));
    tmpBasket.setCurrency(paymentRequest.getPayment().getAmount().getCurrency());
    tmpBasket.setStatus(BasketStatus.PAY_PENDING);

    basketOutPort.updateBasket(tmpBasket);

    return paymentResponse;
  }

  /**
   * Extracts the total cost of the reservations and converts to minor units (ex: cents).
   */
  private BigDecimal totalCost(ReservationByBasketRefResponse reservations, Basket basket) {
    BigDecimal cost;
    if (Boolean.TRUE.equals(basket.getIsCheckInOnlinePay())) {
      cost = reservations.getBalanceOutstanding() != null ? reservations.getBalanceOutstanding() :
          reservations.getTotalCost();
    } else {
      cost = reservations.getTotalCost() != null ? reservations.getTotalCost() :
          reservations.getBalanceOutstanding();
    }
    if (cost == null) {
      var message = String.format("Total reservation costs missing for basketReference=%s",
          basket.getBasketId());
      var exception = new PaymentException(ErrorCode.DIGITAL_INVALID_TOTAL_COST_EXCEPTION, message);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
    return cost.multiply(BigDecimal.valueOf(100));
  }

  private boolean isExpired(Basket basket) {
    final long now = Instant.now().truncatedTo(ChronoUnit.SECONDS).toEpochMilli();
    final long createdAt = Instant.parse(basket.getCreatedAt()).toEpochMilli();
    return now - createdAt > basketValidity;
  }

  private BookingConfirmationDetails getConfirmationPaymentDetails(Basket basket,
      PaymentResponse paymentResponse) {
    HotelPaymentInformation hotelPaymentInformation =
        contentOutPort.getHotelPaymentDetails(basket.getHotelId(),
            Country.valueOf(paymentResponse.getBooking().getLanguage().toUpperCase())
                .getCountryCode(),
            paymentResponse.getBooking().getLanguage().toLowerCase());
    return createConfirmationPaymentDetails(basket.getChannel(), hotelPaymentInformation,
        paymentResponse, unleashWrapper, null);
  }

  private BookingConfirmationDetails getDistributionConfirmationPaymentDetails(Basket basket,
      PaymentRequest paymentRequest) {
    HotelPaymentInformation hotelPaymentInformation =
        contentOutPort.getHotelPaymentDetails(basket.getHotelId(),
            Country.valueOf(paymentRequest.getBooking().getLanguage().toUpperCase())
                .getCountryCode(),
            paymentRequest.getBooking().getLanguage().toLowerCase());
    return createDistributionConfirmationPaymentDetails(basket.getChannel(),
        hotelPaymentInformation,
        paymentRequest,
        unleashWrapper);
  }

  private List<String> getReservationIds(Basket basket) {
    List<BasketItem> basketItems = basket.getItems();

    return Optional.ofNullable(basketItems).map(
        item -> item.stream().map(BasketItem::getSourceId).collect(Collectors.toSet()).stream()
            .toList()).orElse(Collections.emptyList());
  }

  private void updateFailedBasket(Basket basket, BasketStatus status) {
    basket.setStatus(status);
    basket.setCleanUpTime(cleanUpTime.getCleanUpTime(basket));
    basket.setRetryPayment(Boolean.TRUE);
    basketOutPort.updateBasket(basket);
  }

  private boolean validatePrePaidBooking(PaymentRequest paymentRequest) {
    return null != paymentRequest.getPayment().getBilling()
        && paymentRequest.getPayment().getBilling().isBookerIsNotGuest();
  }

  private void preventCiolCharge(Basket basket, PaymentRequest paymentRequest, String basketReference) {
    if (!"3rd Party".equalsIgnoreCase(basket.getIdContext()) || !paymentRequest.isCiol()) {
      return;
    }

    final var reservationByBasketRefResponse =
        reservationOutPort.getReservationsByBasketReference(basket.getBasketId(),
            Boolean.FALSE.toString(), true, false);

    final var routingAmount = Optional.ofNullable(reservationByBasketRefResponse)
        .map(ReservationByBasketRefResponse::getReservationByIdList)
        .filter(CollectionUtils::isNotEmpty)
        .map(reservations -> reservations.get(0))
        .filter(Objects::nonNull)
        .map(Reservation::getRateInfo)
        .filter(Objects::nonNull)
        .map(RateInfo::getSummary)
        .filter(Objects::nonNull)
        .map(RateInfoSummary::getRouting)
        .orElse(BigDecimal.ZERO);

    final var isBalanceOutstandingZero = Optional.ofNullable(reservationByBasketRefResponse)
        .map(ReservationByBasketRefResponse::getBalanceOutstanding)
        .map(balance -> balance.compareTo(BigDecimal.ZERO) == 0)
        .orElse(false);

    final var isNonPiba = Optional.ofNullable(reservationByBasketRefResponse)
        .map(ReservationByBasketRefResponse::getPaymentMethod)
        .filter(Objects::nonNull)
        .map(paymentMethod -> !List.of("BU", "BD").contains(paymentMethod))
        .orElse(true);

    if (routingAmount.compareTo(BigDecimal.ZERO) > 0 && Boolean.TRUE.equals(isNonPiba)
            && Boolean.TRUE.equals(isBalanceOutstandingZero)) {
      var exception = new PaymentOptionNotValidException(
          ErrorCode.DIGITAL_BASKET_CIOL_THIRDPARTY_PAYMENT_VALIDATION_EXCEPTION,
          String.format("CIOL payment failed for 3rdParty.Basket Reference: %s", basketReference));
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }
}
