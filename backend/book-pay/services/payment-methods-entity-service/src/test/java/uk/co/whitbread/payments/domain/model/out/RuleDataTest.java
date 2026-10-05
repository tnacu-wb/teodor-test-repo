package uk.co.whitbread.payments.domain.model.out;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RuleDataTest {

  public static RuleData createRequest() {
    return RuleData.builder()
        .allowIndividualCards(false)
        .reservation(Reservation.builder()
            .hotelPaymentPolicies(Set.of())
            .build())
        .paymentMethods(new ArrayList<>(List.of(PaymentMethodsTestUtils.getNewCardPaymentMethod())))
        .acceptedCardType(getAcceptedCardTypes())
        .threecAcceptedCardType(getThreecAcceptedCardTypes())
        .defaultPaymentMethod(PaymentMethodsTestUtils.getNewCardPaymentMethod())
        .paypalRequest(new PaypalRequest(true, "testToken", "testId"))
        .paymentMethodsConfiguration(getPaymentMethodsConfigurationList())
        .build();
  }

  public static List<PaymentMethodsConfiguration> getPaymentMethodsConfigurationList(){
    List<PaymentMethodsConfiguration> result = new ArrayList<>();
    PaymentMethodsConfiguration pmc = new PaymentMethodsConfiguration();
    pmc.setCode("PP");
    pmc.setName("PAYPAL");
    pmc.setOperaPaymentMethod("DPP");
    pmc.setListOrder("3");
    pmc.setLogo("/temp/paypal.png");
    pmc.setSupportedChannels("PI, BB");
    pmc.setSupportedBookingTypes("PAY_NOW");
    result.add(pmc);
    return result;
  }

  public static RuleData createRequestForApplePay() {
    return RuleData.builder()
            .allowIndividualCards(false)
            .reservation(Reservation.builder()
                    .hotelPaymentPolicies(Set.of())
                    .build())
            .paymentMethods(new ArrayList<>(List.of(PaymentMethodsTestUtils.getApplePaymentMethod())))
            .acceptedCardType(getAcceptedCardTypes())
            .threecAcceptedCardType(getThreecAcceptedCardTypes())
            .defaultPaymentMethod(PaymentMethodsTestUtils.getNewCardPaymentMethod())
            .paypalRequest(new PaypalRequest(true, "testToken", "testId"))
            .build();
  }

  public static RuleData createRequestForGooglePay() {
    return RuleData.builder()
            .allowIndividualCards(false)
            .reservation(Reservation.builder()
                    .hotelPaymentPolicies(Set.of())
                    .build())
            .paymentMethods(new ArrayList<>(List.of(PaymentMethodsTestUtils.getGooglePaymentMethod())))
            .acceptedCardType(getAcceptedCardTypes())
            .threecAcceptedCardType(getThreecAcceptedCardTypes())
            .defaultPaymentMethod(PaymentMethodsTestUtils.getNewCardPaymentMethod())
            .paypalRequest(new PaypalRequest(true, "testToken", "testId"))
            .build();
  }

  public static RuleData createRequestForPibaEuro() {
    return RuleData.builder()
        .allowIndividualCards(false)
        .reservation(Reservation.builder()
            .hotelPaymentPolicies(Set.of())
            .build())
        .paymentMethods(new ArrayList<>(List.of(PaymentMethodsTestUtils.getNewCardPaymentMethod())))
        .acceptedCardType(getAcceptedCardTypes())
        .threecAcceptedCardType(getThreecAcceptedCardTypes())
        .isPibaEuroEnabled(Boolean.TRUE)
        .paypalRequest(new PaypalRequest())
        .build();
  }

  public static RuleData getCard() {
    return RuleData.builder()
        .allowIndividualCards(false)
        .reservation(Reservation.builder()
            .hotelPaymentPolicies(Set.of())
            .build())
        .paymentMethods(new ArrayList<>(List.of(PaymentMethodsTestUtils.paymentMethodsSavedCard(1))))
        .acceptedCardType(getAcceptedCardTypes())
        .threecAcceptedCardType(getThreecAcceptedCardTypes())
        .paypalRequest(new PaypalRequest())
        .build();
  }


  public static RuleData getCardForPibaEuro() {
    return RuleData.builder()
        .allowIndividualCards(false)
        .reservation(Reservation.builder()
            .hotelPaymentPolicies(Set.of())
            .build())
        .paymentMethods(new ArrayList<>(List.of(PaymentMethodsTestUtils.paymentMethodsSavedCard(1))))
        .paypalRequest(new PaypalRequest())
        .acceptedCardType(getAcceptedCardTypes())
        .threecAcceptedCardType(getThreecAcceptedCardTypes())
        .isPibaEuroEnabled(Boolean.TRUE)
        .build();
  }

  public static RuleData getPaymentMethod() {
    return RuleData.builder()
        .paymentMethods(PaymentMethodsTestUtils.paymentMethods())
        .bookingAllowances(PaymentMethodsTestUtils.bookingAllowances())
        .build();
  }

  public static RuleData getPaymentMethodForPibaEURO() {
    return RuleData.builder()
        .paymentMethods(PaymentMethodsTestUtils.paymentMethods())
        .bookingAllowances(PaymentMethodsTestUtils.bookingAllowances())
        .isPibaEuroEnabled(Boolean.TRUE)
        .build();
  }

  public static RuleData createRequestWithSavedCardPaymentMethod(Country country) {
    return RuleData.builder()
        .country(country)
        .paymentMethods(PaymentMethodsTestUtils.paymentMethods())
        .build();
  }

  public static RuleData createRequestWithNewCardPaymentMethodAndCountry() {
    List<PaymentMethod> paymentMethods = new ArrayList<>();
    paymentMethods.add(PaymentMethodsTestUtils.getNewCardPaymentMethod());
    return RuleData.builder()
        .country(Country.DE)
        .paymentMethods(paymentMethods)
        .threecAcceptedCardType(getThreecAcceptedCardTypes())
        .defaultPaymentMethod(PaymentMethodsTestUtils.getNewCardPaymentMethod())
        .reservation(Reservation.builder()
            .hotelPaymentPolicies(Set.of())
            .build())
        .acceptedCardType(getAcceptedCardTypes())
        .paypalRequest(new PaypalRequest(true, "testToken", "testClientID"))
        .paymentMethodsConfiguration(getPaymentMethodsConfigurationList())
        .build();
  }

  public static RuleData createRequestWithNewCardPaymentMethodAndCountryForPibaEuro() {
    List<PaymentMethod> paymentMethods = new ArrayList<>();
    paymentMethods.add(PaymentMethodsTestUtils.getNewCardPaymentMethod());
    return RuleData.builder()
        .country(Country.DE)
        .paymentMethods(paymentMethods)
        .threecAcceptedCardType(getThreecAcceptedCardTypes())
        .reservation(Reservation.builder()
            .hotelPaymentPolicies(Set.of())
            .build())
        .acceptedCardType(getAcceptedCardTypes())
        .isPibaEuroEnabled(Boolean.TRUE)
        .paypalRequest(new PaypalRequest(true, "test", "test"))
        .paymentMethodsConfiguration(getPaymentMethodsConfigurationList())
        .build();
  }

  public static RuleData createRequestWithDepartureData() {
    List<PaymentMethod> paymentMethods = new ArrayList<>();
    paymentMethods.add(PaymentMethodsTestUtils.paymentMethodsSavedCard(1));
    paymentMethods.add(PaymentMethodsTestUtils.getPaypalPaymentMethod());
    return RuleData.builder()
        .acceptedCardType(RuleDataTest.getAcceptedCardTypes())
        .threecAcceptedCardType(RuleDataTest.getThreecAcceptedCardTypes())
        .defaultPaymentMethod(PaymentMethodsTestUtils.getNewCardPaymentMethod())
        .paypalRequest(new PaypalRequest(true, "testToken", "testClientID"))
        .paymentMethods(paymentMethods)
        .paymentMethodsConfiguration(getPaymentMethodsConfigurationList())
        .reservation(Reservation.builder()
            .hotelPaymentPolicies(Set.of())
            .departureDate(LocalDate.of(2021, 11, 25))
            .build())
        .build();
  }

  public static RuleData createRequestWithDepartureDataForPibaEuor() {
    List<PaymentMethod> paymentMethods = new ArrayList<>();
    paymentMethods.add(PaymentMethodsTestUtils.paymentMethodsSavedCard(1));
    return RuleData.builder()
        .acceptedCardType(RuleDataTest.getAcceptedCardTypes())
        .threecAcceptedCardType(RuleDataTest.getThreecAcceptedCardTypes())
        .paymentMethods(paymentMethods)
        .reservation(Reservation.builder()
            .hotelPaymentPolicies(Set.of())
            .departureDate(LocalDate.of(2021, 11, 25))
            .build())
        .isPibaEuroEnabled(Boolean.TRUE)
        .build();
  }

  public static RuleData createRequestWithNewPibaPaymentMethod() {

    List<PaymentMethod> paymentMethods = new ArrayList<>();
    paymentMethods.add(PaymentMethodsTestUtils.getNewPibaPaymentMethod("PIBA UK", "PIBAGB"));

    return RuleData.builder()
        .threecAcceptedCardType(getThreecAcceptedCardTypes())
        .acceptedCardType(getAcceptedCardTypes())
        .reservation(Reservation.builder()
            .hotelPaymentPolicies(Set.of())
            .build())
        .paymentMethods(paymentMethods)
        .paypalRequest(new PaypalRequest(true, "test", "test"))
        .paymentMethodsConfiguration(getPaymentMethodsConfigurationList())
        .build();
  }
  public static RuleData createRequestWithNewPibaPaymentMethodForPibaEuro() {
    List<PaymentMethod> paymentMethods = new ArrayList<>();
    paymentMethods.add(PaymentMethodsTestUtils.getNewPibaPaymentMethod("PIBA UK", "PIBAGB"));

    return RuleData.builder()
        .threecAcceptedCardType(getThreecAcceptedCardTypes())
        .acceptedCardType(getAcceptedCardTypes())
        .reservation(Reservation.builder()
            .hotelPaymentPolicies(Set.of())
            .build())
        .paymentMethods(paymentMethods)
        .isPibaEuroEnabled(Boolean.TRUE)
        .paypalRequest(new PaypalRequest(true, "test", "test"))
        .paymentMethodsConfiguration(getPaymentMethodsConfigurationList())
        .build();
  }

  public static RuleData createRequestWithDepartureDataExpired() {
    return RuleData.builder()
        .paymentMethods(PaymentMethodsTestUtils.paymentMethods())
        .reservation(Reservation.builder()
            .departureDate(LocalDate.of(2021, 3, 1))
            .build())
        .build();
  }

  public static RuleData createRequestWithNewCardPaymentMethodRequired() {
    return RuleData.builder()
        .paymentMethods(List.of(PaymentMethodsTestUtils.getNewCardPaymentMethod()))
        .reservation(Reservation.builder()
            .hotelPaymentPolicies(Set.of(PaymentPolicy.PAY_NOW))
            .build())
        .build();
  }

  public static RuleData createRequestWithNewCardForHubHotels() {
    return RuleData.builder()
        .paymentMethods(List.of(PaymentMethodsTestUtils.getNewCardPaymentMethod()))
        .hotelBrand("HUB")
        .reservation(Reservation.builder().ratePlanCode("EMPLOYEE").build())
        .build();
  }

  public static RuleData createRequestWithStoredCardForHubHotels() {
    return RuleData.builder()
        .paymentMethods(PaymentMethodsTestUtils.paymentMethods())
        .hotelBrand("HUB")
        .reservation(Reservation.builder().ratePlanCode("EMPLOYEE").build())
        .build();
  }

  public static List<AcceptedCardType> getAcceptedCardTypes() {
    return List.of(AcceptedCardTypeTest.createAcceptedCardType());
  }

  public static List<AcceptedCardType> getThreecAcceptedCardTypes() {
    return List.of(AcceptedCardTypeTest.createAcceptedCardType());
  }

  public static RuleData getPaymentMethodSpecificCard(int index, Country country) {
    return RuleData.builder()
            .paymentMethods(List.of(getSavedCardInformation(index)))
            .bookingAllowances(PaymentMethodsTestUtils.bookingAllowances())
            .country(country)
            .isPibaEuroEnabled(true)
            .build();
  }

  private static PaymentMethod getSavedCardInformation(int index){
    PaymentMethod paymentMethod = PaymentMethodsTestUtils.paymentMethodsSavedCard(index);
    PibaCard.resolveSubTypeForPibaCards(paymentMethod.getCard().getType()).ifPresent(pibaCard -> paymentMethod.setSubType(pibaCard.getSubType()));
    return paymentMethod;
  }

  public static RuleData createRequestAgentNewCardPibaEuro(Boolean flag) {
    List<PaymentMethod> paymentMethods = new ArrayList<>();
    var paymentMethod = PaymentMethodsTestUtils.getNewPibaPaymentMethod("PIBA", "PIBADE");
    paymentMethods.add(paymentMethod);
    return RuleData.builder()
            .country(Country.DE)
            .paymentMethods(paymentMethods)
            .threecAcceptedCardType(getThreecAcceptedCardTypes())
            .defaultPaymentMethod(paymentMethod)
            .reservation(Reservation.builder()
                    .hotelPaymentPolicies(Set.of())
                    .build())
            .acceptedCardType(getAcceptedCardTypes())
            .paypalRequest(new PaypalRequest(true, "testToken", "testClientID"))
            .isPibaEuroEnabled(flag)
            .build();
  }

  public static RuleData createRequestAgentNewCardPiba(Boolean flag) {
    List<PaymentMethod> paymentMethods = new ArrayList<>();
    var paymentMethod = PaymentMethodsTestUtils.getNewPibaPaymentMethod("PIBA", "PIBAUK");
    paymentMethods.add(paymentMethod);
    return RuleData.builder()
            .country(Country.GB)
            .paymentMethods(paymentMethods)
            .threecAcceptedCardType(getThreecAcceptedCardTypes())
            .defaultPaymentMethod(paymentMethod)
            .reservation(Reservation.builder()
                    .hotelPaymentPolicies(Set.of())
                    .build())
            .acceptedCardType(getAcceptedCardTypes())
            .paypalRequest(new PaypalRequest(true, "testToken", "testClientID"))
            .isPibaEuroEnabled(flag)
            .build();
  }

  // ── Datatrans helpers ─────────────────────────────────────────────────────

  /** Returns a minimal list of Datatrans-coded AcceptedCardType objects. */
  public static List<AcceptedCardType> getDatatransAcceptedCardTypes() {
    AcceptedCardType eca = new AcceptedCardType();
    eca.setType("ECA");
    eca.setName("Mastercard (Datatrans)");

    AcceptedCardType vis = new AcceptedCardType();
    vis.setType("VIS");
    vis.setName("Visa (Datatrans)");

    return List.of(eca, vis);
  }

  /**
   * RuleData with isDataTransEnabled=true and NEW_CARD as a Datatrans-eligible option.
   * The exclusion list only contains SAVED_CARD so NEW_CARD routes to Datatrans.
   */
  public static RuleData createRequestWithDatatransEnabled() {
    return RuleData.builder()
        .allowIndividualCards(false)
        .reservation(Reservation.builder()
            .hotelPaymentPolicies(Set.of())
            .build())
        .paymentMethods(new ArrayList<>(List.of(PaymentMethodsTestUtils.getNewCardPaymentMethod())))
        .acceptedCardType(getAcceptedCardTypes())
        .threecAcceptedCardType(getThreecAcceptedCardTypes())
        .datatransAcceptedCardType(getDatatransAcceptedCardTypes())
        .isDataTransEnabled(true)
        .dataTransProperties(new DataTransConfig(List.of("SAVED_CARD")))
        .defaultPaymentMethod(PaymentMethodsTestUtils.getNewCardPaymentMethod())
        .paypalRequest(new PaypalRequest(true, "testToken", "testId"))
        .paymentMethodsConfiguration(getPaymentMethodsConfigurationList())
        .build();
  }

  /**
   * RuleData with isDataTransEnabled=true but NEW_CARD in the exclusion list,
   * so NEW_CARD still routes to 3CP.
   */
  public static RuleData createRequestWithDatatransEnabledButNewCardExcluded() {
    return RuleData.builder()
        .allowIndividualCards(false)
        .reservation(Reservation.builder()
            .hotelPaymentPolicies(Set.of())
            .build())
        .paymentMethods(new ArrayList<>(List.of(PaymentMethodsTestUtils.getNewCardPaymentMethod())))
        .acceptedCardType(getAcceptedCardTypes())
        .threecAcceptedCardType(getThreecAcceptedCardTypes())
        .datatransAcceptedCardType(getDatatransAcceptedCardTypes())
        .isDataTransEnabled(true)
        .dataTransProperties(new DataTransConfig(List.of("SAVED_CARD", "NEW_CARD")))
        .defaultPaymentMethod(PaymentMethodsTestUtils.getNewCardPaymentMethod())
        .paypalRequest(new PaypalRequest(true, "testToken", "testId"))
        .paymentMethodsConfiguration(getPaymentMethodsConfigurationList())
        .build();
  }

  public static RuleData createRequestWithForPayments(final PaymentPolicy hotelPaymentPolicy,
                                                    final LocalDate arrivalDate,
                                                    final Country country,
                                                    final boolean isCcuiDeHotelPaymentsWithin72hEnabled,
                                                    final boolean isCcuiUkHotelPaymentsWithin72hEnabled,
                                                    final List<PaymentMethod> paymentMethods) {
    return RuleData.builder()
          .paymentMethods(paymentMethods)
          .reservation(Reservation.builder()
                .arrivalDate(arrivalDate)
                .hotelPaymentPolicies(Set.of(hotelPaymentPolicy))
                .build())
          .changePaymentBIC(false)
          .country(country)
          .isCcuiDeHotelPaymentsWithin72hEnabled(isCcuiDeHotelPaymentsWithin72hEnabled)
          .isCcuiUkHotelPaymentsWithin72hEnabled(isCcuiUkHotelPaymentsWithin72hEnabled)
          .build();
  }

}
