package uk.co.whitbread.payments.domain.logic;


import static uk.co.whitbread.payments.domain.logic.rule.RuleEngineSelector.selectCcuiExecutor;
import static uk.co.whitbread.payments.domain.model.out.CardOption.ACCOUNT_COMPANY;
import static uk.co.whitbread.payments.domain.model.out.PaymentPolicy.PAY_ON_ARRIVAL;

import io.getunleash.UnleashContext;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.payments.domain.logic.common.PaymentMethodsCommon;
import uk.co.whitbread.payments.domain.model.feature.FeatureFlag;
import uk.co.whitbread.payments.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.payments.domain.model.in.PaymentMethodsRequest;
import uk.co.whitbread.payments.domain.model.out.Country;
import uk.co.whitbread.payments.domain.model.out.DataTransConfig;
import uk.co.whitbread.payments.domain.model.out.HotelInfo;
import uk.co.whitbread.payments.domain.model.out.PaymentMethod;
import uk.co.whitbread.payments.domain.model.out.PaymentMethods;
import uk.co.whitbread.payments.domain.model.out.Reservation;
import uk.co.whitbread.payments.domain.model.out.RuleData;
import uk.co.whitbread.payments.domain.ports.primary.PaymentsCcuiMethodsPort;
import uk.co.whitbread.payments.domain.ports.secondary.DefaultPaymentMethodsPort;
import uk.co.whitbread.payments.domain.ports.secondary.HotelInfoPort;
import uk.co.whitbread.payments.domain.ports.secondary.ReservationsPort;

@Slf4j
@RequiredArgsConstructor
public class PaymentCcuiMethodsPortImpl implements PaymentsCcuiMethodsPort {
  private final HotelInfoPort hotelInfoPort;
  private final DefaultPaymentMethodsPort defaultPaymentMethodsPort;
  private final ReservationsPort reservationPort;
  private final PaymentMethodsCommon paymentMethodsCommon;
  private final boolean pibaEuroFlag;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;
  private final DataTransConfig dataTransProperties;

  private static final String UNLEASH_CONTEXT_BASKET_REFERENCE = "basketReference";

  @Override
  public PaymentMethods getPaymentMethods(PaymentMethodsRequest request) {
    final var reservation = reservationPort.findReservations(request.getBasketReference(), false);
    final var hotelInfo = hotelInfoPort.findHotelPaymentDetails(reservation.getHotelId(),
        request.getCountry().toLowerCase(),
        request.getLanguage().toLowerCase());

    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getDisablePayments(),
        UnleashContext.builder().addProperty(UNLEASH_CONTEXT_BASKET_REFERENCE, request.getBasketReference()).build())) {
      log.warn("Feature flag kill_switch_pi_bb_ccui_disable_payments is enabled. Loaded failsafe paymentMethods "
          + "configuration for basketReference {}", request.getBasketReference());
      return loadFailSafePaymentMethods(request, hotelInfo);
    }

    return loadDefaultPaymentMethods(request, reservation, hotelInfo);
  }

  private PaymentMethods loadFailSafePaymentMethods(PaymentMethodsRequest request, HotelInfo hotelInfo) {
    List<PaymentMethod> failSafePaymentCcuiMethods = defaultPaymentMethodsPort.getFailSafePaymentCcuiMethods();
    updateAccountToCompanyPaymentMethod(request, failSafePaymentCcuiMethods, hotelInfo);
    PaymentMethods paymentMethods = PaymentMethods.builder()
        .paymentMethods(failSafePaymentCcuiMethods)
        .build();
    return paymentMethodsCommon.updatePaymentMethodOrder(paymentMethods);
  }

  private PaymentMethods loadDefaultPaymentMethods(PaymentMethodsRequest request, Reservation reservation,
      HotelInfo hotelInfo) {
    List<PaymentMethod> defaultPaymentCcuiMethods = defaultPaymentMethodsPort.getDefaultPaymentCcuiMethods();
    updateAccountToCompanyPaymentMethod(request, defaultPaymentCcuiMethods, hotelInfo);
    final var ruleExecutor = selectCcuiExecutor(
        createRuleContextDataFrom(reservation, defaultPaymentCcuiMethods, hotelInfo, request));

    return ruleExecutor.execute()
        .map(paymentMethodsCommon::updatePaymentMethodOrder)
        .orElse(null);
  }

  private void updateAccountToCompanyPaymentMethod(PaymentMethodsRequest request, List<PaymentMethod> paymentMethods,
      HotelInfo hotelInfo) {
    var site = request.getCountry();
    var hotelCountry = paymentMethodsCommon.hotelCountry(hotelInfo).name();

    Predicate<String> isUkSite = siteRef -> siteRef.equalsIgnoreCase(Country.GB.name());
    BiPredicate<String, String> isDeSiteUkHotel = (siteRef, hotelCountryRef) ->
        siteRef.equalsIgnoreCase(Country.DE.name()) && hotelCountryRef.equalsIgnoreCase(
            Country.GB.name());
    BiPredicate<String, String> isDeSiteIeHotel = (siteRef, hotelCountryRef) ->
        siteRef.equalsIgnoreCase(Country.DE.name()) && hotelCountryRef.equalsIgnoreCase(
            Country.IE.name());
    BiPredicate<String, String> isDeSiteDeHotel = (siteRef, hotelCountryRef) ->
        siteRef.equalsIgnoreCase(Country.DE.name()) && hotelCountryRef.equalsIgnoreCase(
            Country.DE.name());

    paymentMethods.forEach(paymentMethod -> {
      if (ACCOUNT_COMPANY.name().equals(paymentMethod.getType())) {
        if (isUkSite.test(site)
            || isDeSiteUkHotel.test(site, hotelCountry) || isDeSiteIeHotel.test(site,
            hotelCountry)) {
          paymentMethod.setEnabled(false);
          paymentMethod.getPaymentOptions().stream()
              .filter(paymentOption -> PAY_ON_ARRIVAL.name().equals(paymentOption.getType()))
              .forEach(paymentOption -> paymentOption.setEnabled(false));
        } else if (isDeSiteDeHotel.test(site, hotelCountry)) {
          paymentMethod.setEnabled(true);
        }
      }
    });
  }

  private RuleData createRuleContextDataFrom(final Reservation reservation,
      final List<PaymentMethod> defaultPaymentMethods,
      final HotelInfo hotelInfo,
      final PaymentMethodsRequest request) {
    return RuleData.builder().threecAcceptedCardType(paymentMethodsCommon.acceptedCards(hotelInfo))
        .acceptedCardType(paymentMethodsCommon.acceptedCardTypes(hotelInfo))
        .paymentMethods(new ArrayList<>(defaultPaymentMethods))
        .reservation(reservation)
        .isPibaEuroEnabled(pibaEuroFlag)
        .country(paymentMethodsCommon.hotelCountry(hotelInfo))
        .userType(request.getUserType())
        .changePaymentBIC(request.isChangePaymentBIC())
        .isCcuiDeHotelPaymentsWithin72hEnabled(
            unleashWrapper.isEnabled(
                unleashWrapper.featureFlag().getCcuiDeEnablePaymentsWithin72h()))
        .isCcuiUkHotelPaymentsWithin72hEnabled(
            unleashWrapper.isEnabled(
                unleashWrapper.featureFlag().getCcuiUkEnablePaymentsWithin72h()))
        .isPoaForHubEnabled(
            unleashWrapper.isEnabled(unleashWrapper.featureFlag().getEnableHubHotelsPoa())
        )
        .isDataTransEnabled(Boolean.TRUE.equals(hotelInfo.getIsDataTransEnabled()))
        .datatransAcceptedCardType(paymentMethodsCommon.datatransAcceptedCards(hotelInfo))
        .dataTransProperties(dataTransProperties)
        .build();
  }
}

