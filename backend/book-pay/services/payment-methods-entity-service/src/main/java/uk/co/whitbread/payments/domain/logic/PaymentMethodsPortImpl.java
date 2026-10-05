package uk.co.whitbread.payments.domain.logic;

import static uk.co.whitbread.payments.domain.logic.common.PaymentMethodsConstant.EMPLOYEE_RATE_PLAN_CODE;
import static uk.co.whitbread.payments.domain.logic.rule.RuleEngineSelector.selectExecutor;
import static uk.co.whitbread.payments.domain.model.in.UserType.BUSINESS;
import static uk.co.whitbread.payments.domain.model.out.CardOption.AP;
import static uk.co.whitbread.payments.domain.model.out.CardOption.GP;
import static uk.co.whitbread.payments.domain.model.out.CardOption.NEW_CARD;
import static uk.co.whitbread.payments.domain.model.out.CardOption.NEW_PIBA;
import static uk.co.whitbread.payments.domain.model.out.CardOption.PAYPAL;
import static uk.co.whitbread.payments.domain.model.out.CardOption.SAVED_CARD;
import static uk.co.whitbread.payments.domain.model.out.PaymentPolicy.RESERVE_WITHOUT_CARD;

import io.getunleash.UnleashContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.payments.domain.exception.ErrorCode;
import uk.co.whitbread.payments.domain.exception.PaymentMethodsException;
import uk.co.whitbread.payments.domain.exception.PaymentMethodsValidationException;
import uk.co.whitbread.payments.domain.logic.common.PaymentMethodsCommon;
import uk.co.whitbread.payments.domain.model.feature.FeatureFlag;
import uk.co.whitbread.payments.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.payments.domain.model.in.PaymentMethodsFlow;
import uk.co.whitbread.payments.domain.model.in.PaymentMethodsRequest;
import uk.co.whitbread.payments.domain.model.in.SelectedPaymentMethods;
import uk.co.whitbread.payments.domain.model.out.Card;
import uk.co.whitbread.payments.domain.model.out.CardType;
import uk.co.whitbread.payments.domain.model.out.CompanyDetailsResponse;
import uk.co.whitbread.payments.domain.model.out.DataTransConfig;
import uk.co.whitbread.payments.domain.model.out.HotelInfo;
import uk.co.whitbread.payments.domain.model.out.PaymentMethod;
import uk.co.whitbread.payments.domain.model.out.PaymentMethods;
import uk.co.whitbread.payments.domain.model.out.PaymentMethodsConfiguration;
import uk.co.whitbread.payments.domain.model.out.PaypalRequest;
import uk.co.whitbread.payments.domain.model.out.PibaCard;
import uk.co.whitbread.payments.domain.model.out.Reservation;
import uk.co.whitbread.payments.domain.model.out.RuleData;
import uk.co.whitbread.payments.domain.ports.primary.PaymentMethodsPort;
import uk.co.whitbread.payments.domain.ports.secondary.BasketPort;
import uk.co.whitbread.payments.domain.ports.secondary.CustomerAccountPort;
import uk.co.whitbread.payments.domain.ports.secondary.DefaultPaymentMethodsPort;
import uk.co.whitbread.payments.domain.ports.secondary.HotelInfoPort;
import uk.co.whitbread.payments.domain.ports.secondary.ReservationsPort;
import uk.co.whitbread.payments.domain.ports.secondary.TokenPort;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;

@Slf4j
@RequiredArgsConstructor
public class PaymentMethodsPortImpl implements PaymentMethodsPort {

  private static final String PIBA_CARD = "PIBA";
  private static final String LEISURE_CARD = "CARD";
  private static final String CHANNEL_DISTR = "DISTR";
  private static final String HUB_HOTEL = "HUB";
  private static final String DEFAULT_COUNTRY = "gb";
  private static final String DEFAULT_LANGUAGE = "en";
  private static final String PAYPAL_CODE = "PP";
  public static final String UNDEFINED = "undefined";
  private static final String UNLEASH_CONTEXT_BASKET_REFERENCE = "basketReference";
  private static final String APPS_ANDROID = "APPS_ANDROID";
  private static final String APPS_IOS = "APPS_IOS";
  private static final String COMPLETED = "COMPLETED";
  private static final String CIOL_FAILED = "CIOL_FAILED";
  private static final String CIOL_RC_FAILED = "CIOL_RC_FAILED";
  private final HotelInfoPort hotelInfoPort;
  private final CustomerAccountPort customerAccountPort;
  private final DefaultPaymentMethodsPort defaultPaymentMethodsPort;
  private final ReservationsPort reservationPort;
  private final PaymentMethodsCommon paymentMethodsCommon;
  private final AuthenticatedUserService authenticatedUserService;
  private final TokenPort tokenPort;
  private final BasketPort basketPort;
  private final boolean pibaEuroFlag;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;
  private final DataTransConfig dataTransProperties;
  private final List<String> pibaPaymentTypes = List.of("BU", "BD");
  private static final String PAY_PENDING = "PAY_PENDING";



  @Override
  public PaymentMethods getPaymentMethods(final PaymentMethodsRequest request) {
    final var basketReservation = reservationPort.findBasketReservations(request.getBasketReference());
    final var reservation = Reservation.builder()
        .hotelId(basketReservation.getHotelId())
        .hotelPaymentPolicies(basketReservation.getHotelPaymentPolicies())
        .departureDate(basketReservation.getDepartureDate())
        .build();
    final var hotelInfo = hotelInfoPort.findHotelPaymentDetails(reservation.getHotelId(),
        request.getCountry().toLowerCase(), request.getLanguage().toLowerCase());
    List<Card> savedCards = List.of();
    Optional<CompanyDetailsResponse> company = Optional.empty();

    //only for authenticated journeys
    if (authenticatedUserService.isUserAuthenticated()) {
      var currentUserAccount = authenticatedUserService.getCurrentUserAccount();
      if (currentUserAccount.isEmpty()) {
        var message = "Error while obtaining user account from token.";
        var exception = new PaymentMethodsException(ErrorCode.DIGITAL_USER_ACCOUNT_NOT_OBTAINED_EXCEPTION,
            message);
        ExceptionLogger.log(log, exception);
        throw exception;
      }
      String userId = currentUserAccount.get().getEmail();
      var account = customerAccountPort.findCustomerAccount(userId,
          request.getUserType(),
          request.getBookingChannel(),
          request.getAuthorization());
      if (BUSINESS.equals(request.getUserType())) {
        //set the companyId and employeeId from the token to get the cdh data and not the bart data!
        account.setCompanyId(currentUserAccount.get().getCompanyId());
        account.getBusiness().setEmployeeId(currentUserAccount.get().getEmployeeId());
        company = customerAccountPort.findCompany(account, request.getAuthorization());
      }
      savedCards = customerAccountPort.findCustomerSavedCards(userId,
          request.getUserType(), request.getBookingChannel(), account, request.getAuthorization());
    }
    PaymentMethodsFlow paymentFlow = PaymentMethodsFlow.valueOfPaymentMethod(request.getFlow());

    var isApps = request.getClientChannel() != null
        && List.of(APPS_ANDROID, APPS_IOS).contains((request.getClientChannel()))
        && paymentFlow != null && PaymentMethodsFlow.CHECKINONLINE.equals(paymentFlow);

    var isCiolFlow = false;
    if (isApps) {
      var statuses = List.of(COMPLETED, CIOL_FAILED, CIOL_RC_FAILED, PAY_PENDING);
      var basket = basketPort.getBasket(request.getBasketReference());
      var isStatus = basket != null && basket.getBasketStatus() != null
          && statuses.contains(basket.getBasketStatus());
      isCiolFlow = isStatus;
    }
    boolean isPibaType =
        pibaPaymentTypes.contains(basketReservation.getPaymentMethod())
            && Integer.valueOf(1).equals(basketReservation.getFolioView());

    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getDisablePayments(),
        UnleashContext.builder()
            .addProperty(UNLEASH_CONTEXT_BASKET_REFERENCE, request.getBasketReference()).build())) {
      var defaultPaymentMethods = loadFailSafePaymentMethods(request);
      return isCiolFlow ? filterByAppsPaymentMethods(defaultPaymentMethods, isPibaType)
          : defaultPaymentMethods;
    }

    var defaultPaymentMethods = loadDefaultPaymentMethods(
        reservation, hotelInfo, request, savedCards, company);

    return isCiolFlow ? filterByAppsPaymentMethods(defaultPaymentMethods, isPibaType)
        : defaultPaymentMethods;
  }

  private PaymentMethods loadFailSafePaymentMethods(PaymentMethodsRequest request) {
    log.warn(
        "Feature flag kill_switch_pi_bb_ccui_disable_payments is enabled. Loaded failsafe paymentMethods "
            + "configuration for basketReference {}",
        request.getBasketReference());
    List<PaymentMethod> failSafePaymentMethods = defaultPaymentMethodsPort.getFailSafePaymentMethods();
    PaymentMethods paymentMethods = PaymentMethods.builder()
        .paymentMethods(failSafePaymentMethods)
        .build();
    paymentMethods = paymentMethodsCommon.updatePaymentMethodOrder(paymentMethods);
    setLogoSrcUndefinedIfBlank(paymentMethods);

    return paymentMethods;
  }

  private PaymentMethods loadDefaultPaymentMethods(Reservation reservation, HotelInfo hotelInfo,
                                                   PaymentMethodsRequest request, List<Card> savedCards,
                                                   Optional<CompanyDetailsResponse> company) {
    List<PaymentMethod> defaultPaymentMethods = defaultPaymentMethodsPort.getDefaultPaymentMethods();
    final var ruleExecutor = selectExecutor(
        createRuleContextDataFrom(request, defaultPaymentMethods, savedCards, company, reservation, hotelInfo));

    PaymentMethods paymentMethods = ruleExecutor.execute()
        .map(paymentMethodsCommon::updatePaymentMethodOrder)
        .orElse(null);
    setLogoSrcUndefinedIfBlank(paymentMethods);

    return paymentMethods;
  }

  /**
   * Filters the default payment methods to include only NEW_CARD option for APPS channels during
   * Check-In Online flow.
   *
   * @param defaultPaymentMethods the default payment methods
   * @return the payment methods containing only NEW_CARD option
   */
  private PaymentMethods filterByAppsPaymentMethods(PaymentMethods defaultPaymentMethods, boolean isPibaType) {
    List<String> cardOptions = new ArrayList<>(List.of(NEW_CARD.name(), GP.name(),
        AP.name(), PAYPAL.name(), SAVED_CARD.name()));
    if (isPibaType) {
      cardOptions.add(NEW_PIBA.name());
      cardOptions.remove(SAVED_CARD.name());
    }

    var paymentMethodsList = defaultPaymentMethods.getPaymentMethods().stream().filter(
        paymentMethod -> cardOptions.contains(paymentMethod.getType())).toList();

    return PaymentMethods.builder().paymentMethods(paymentMethodsList).build();
  }

  private RuleData createRuleContextDataFrom(PaymentMethodsRequest request, List<PaymentMethod> defaultPaymentMethods,
      List<Card> savedCards, Optional<CompanyDetailsResponse> company,
                                             Reservation reservation, HotelInfo hotelInfo) {
    return RuleData.builder()
        .threecAcceptedCardType(paymentMethodsCommon.acceptedCards(hotelInfo))
        .userType(request.getUserType())
        .acceptedCardType(paymentMethodsCommon.acceptedCardTypes(hotelInfo))
        .paymentMethods(paymentMethods(defaultPaymentMethods, savedCards))
        .reservation(reservation)
        .country(paymentMethodsCommon.hotelCountry(hotelInfo))
        .allowIndividualCards(company.map(
            companyDetailsResponse -> companyDetailsResponse.getRequestedCompany()
                .getBookingAllowances().isAllowIndividualCards()).orElse(true))
        .allowCentralCreditCard(
            company.map(CompanyDetailsResponse::isAllowCentralCreditCard).orElse(false))
        .bookingAllowances(company.map(
            companyDetailsResponse -> companyDetailsResponse.getRequestedCompany()
                .getBookingAllowances()).orElse(null))
        .hotelBrand(hotelInfo.getBrand())
        .channelId(request.getBookingChannel())
        .hotelId(reservation.getHotelId())
        .paypalRequest(getPaypalData(request, hotelInfo))
        .paymentMethodsConfiguration(hotelInfo.getPaymentMethodsConfiguration())
        .defaultPaymentMethod(defaultPaymentMethodsPort.getDefaultPaymentMethods().get(0))
        .isPibaEuroEnabled(pibaEuroFlag)
        .clientChannel(request.getClientChannel())
        .isPoaForHubEnabled(
            unleashWrapper.isEnabled(unleashWrapper.featureFlag().getEnableHubHotelsPoa())
        )
        .isDataTransEnabled(Boolean.TRUE.equals(hotelInfo.getIsDataTransEnabled()))
        .datatransAcceptedCardType(paymentMethodsCommon.datatransAcceptedCards(hotelInfo))
        .dataTransProperties(dataTransProperties)
        .build();
  }

  /**
   * Sets the logo source of the payment methods to "UNDEFINED" if any of them have a blank logo source.
   * This is to ensure that the UI does not attempt to load a blank image.
   *
   * @param paymentMethods the payment methods object to check and update the logo source for
   */
  public void setLogoSrcUndefinedIfBlank(PaymentMethods paymentMethods) {
    Optional.ofNullable(paymentMethods)
        .map(PaymentMethods::getPaymentMethods)
        .ifPresent(methods -> methods.stream().filter(Objects::nonNull).forEach(paymentMethod ->
            Optional.ofNullable(paymentMethod.getAcceptedCardTypes())
                .ifPresent(acceptedCardTypes ->
                    acceptedCardTypes.stream()
                        .filter(acceptedCardType ->
                            acceptedCardType != null && StringUtils.isBlank(acceptedCardType.getLogoSrc()))
                        .forEach(acceptedCardType -> acceptedCardType.setLogoSrc(UNDEFINED)))));
  }

  @Override
  public void validatePaymentMethods(SelectedPaymentMethods request) {
    final var reservation = reservationPort.findReservations(request.getBasketReference(), true);
    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getDisablePayments(),
        UnleashContext.builder().addProperty(UNLEASH_CONTEXT_BASKET_REFERENCE, request.getBasketReference()).build())
        && request.getSelectedPaymentOption().equals(RESERVE_WITHOUT_CARD)) {
      log.warn("Feature flag kill_switch_pi_bb_ccui_disable_payments is enabled. "
          + "Allowing RESERVE_WITHOUT_CARD for basketReference " + request.getBasketReference());
      return;
    }

    if (CHANNEL_DISTR.equals(reservation.getChannel())
        && HUB_HOTEL.equals(
        hotelInfoPort.findHotelPaymentDetails(reservation.getHotelId(), DEFAULT_COUNTRY, DEFAULT_LANGUAGE)
            .getBrand())) {
      return;
    }

    if (Boolean.TRUE.equals(request.getIsCiol())) {
      return;
    }

    if (!StringUtils.equalsIgnoreCase(EMPLOYEE_RATE_PLAN_CODE, reservation.getRatePlanCode())
        && !reservation.getHotelPaymentPolicies().contains(request.getSelectedPaymentOption())
        && !PIBA_CARD.equals(request.getType())) {
      Map<String, String> validationErrors = new HashMap<>();
      validationErrors.put("selected.payment.option",
            "Payment method " + request.getSelectedPaymentOption()
                  + " is not allowed for basketReference " + request.getBasketReference());

      var message = String.format("Error while validating payment methods, "
                  + "we don't allow this payment option=%s for basketReference %s",
            request.getSelectedPaymentOption(), request.getBasketReference()) + validationErrors;
      var exception = new PaymentMethodsValidationException(ErrorCode.DIGITAL_VALIDATE_PAYMENT_EXCEPTION,
          message);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }

  private List<PaymentMethod> paymentMethods(final List<PaymentMethod> defaultPaymentMethods,
                                             final List<Card> cards) {
    final var paymentMethodList = new LinkedList<>(defaultPaymentMethods);
    if (!ObjectUtils.isEmpty(cards)) {
      cards.forEach(card -> paymentMethodList.addFirst(cardPaymentMethod(card)));
    }
    return paymentMethodList;
  }

  private PaymentMethod cardPaymentMethod(final Card card) {
    final var method = new PaymentMethod();
    method.setName(CardType.isBusinessCard(card.getType()) ? PIBA_CARD : LEISURE_CARD);
    method.setType(SAVED_CARD.name());
    PibaCard.resolveSubTypeForPibaCards(card.getType()).ifPresent(pibaCard -> method.setSubType(pibaCard.getSubType()));
    method.setEnabled(Boolean.TRUE);
    method.setCard(card);
    method.setCnpPreSelected(card.isCnpRequired());
    method.setPaymentOptions(defaultPaymentMethodsPort.getSavedCardPaymentOptions());
    return method;
  }

  private PaypalRequest getPaypalData(PaymentMethodsRequest request, HotelInfo hotelInfo) {
    PaypalRequest paypalRequest = new PaypalRequest(false, "", "");
    var paypalConfig = hotelInfo.getPaymentMethodsConfiguration();
    try {
      Optional<PaymentMethodsConfiguration> paypalPaymentConfigDTO =
          Optional.ofNullable(paypalConfig)
              .filter(list -> !list.isEmpty())
              .flatMap(list -> list.stream()
                  .filter(dto -> PAYPAL_CODE.equalsIgnoreCase(dto.getCode())
                      && dto.getSupportedChannels().contains(request.getClientChannel())).findFirst());
      paypalPaymentConfigDTO.ifPresent(dto -> {
        var paypalClientDetails = tokenPort.getPaypalToken(paymentMethodsCommon.hotelCountry(hotelInfo).name());
        if (paypalClientDetails != null) {
          paypalRequest.setPaypalEnabled(true);
          paypalRequest.setClientId(paypalClientDetails.getClientId());
          paypalRequest.setClientToken(paypalClientDetails.getClientToken());
        }
      });
    } catch (Exception e) {
      log.error("Error encountered with retrieving PayPal details", e);
      return new PaypalRequest(false, "", "");
    }
    return paypalRequest;
  }

}