package uk.co.whitbread.payments.domain.logic.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.payments.domain.model.out.AcceptedCardType;
import uk.co.whitbread.payments.domain.model.out.DataTransConfig;
import uk.co.whitbread.payments.domain.model.out.PaymentMethod;
import uk.co.whitbread.payments.domain.model.out.PaymentMethodsConfiguration;
import uk.co.whitbread.payments.domain.model.out.PaymentOption;
import uk.co.whitbread.payments.domain.model.out.PaymentProviderType;
import uk.co.whitbread.payments.domain.model.out.Reservation;
import uk.co.whitbread.payments.domain.model.out.RuleData;

@Slf4j
@RequiredArgsConstructor
class GooglePaymentRuleTest {

  private GooglePaymentRule googlePaymentRule;

  @BeforeEach
  void init() {
    googlePaymentRule = new GooglePaymentRule();
  }

  @Test
  void hotelIsAllowedToAcceptGooglePay() {

    //Given
    final String hotelId = "GLABAL";

    //When
    var result = googlePaymentRule.calculate(mockBuildRuleData(hotelId, "APPS_ANDROID"));

    //Then
    assertNotNull(result);
    assertEquals(2, result.getThreecAcceptedCardType().size());
  }

  @Test
  void hotelIsAllowedToAcceptGooglePayForPI() {

    //Given
    final String hotelId = "GLABAL";

    //When
    var result = googlePaymentRule.calculate(mockBuildRuleData(hotelId, "PI"));

    //Then
    assertNotNull(result);
    assertEquals(2, result.getThreecAcceptedCardType().size());
  }

  @Test
  void hotelIsAllowedToAcceptGooglePayForBB() {

    //Given
    final String hotelId = "GLABAL";

    //When
    var result = googlePaymentRule.calculate(mockBuildRuleData(hotelId, "BB"));

    //Then
    assertNotNull(result);
    assertEquals(2, result.getThreecAcceptedCardType().size());
  }

  @Test
  void hotelIsAllowedToAcceptGooglePayWithLogoSrc() {

    //Given
    final String hotelId = "GLABAL";
    RuleData ruleData = mockBuildRuleData(hotelId, "APPS_ANDROID");

    //When
    var result = googlePaymentRule.calculate(ruleData);

    //Then
    assertNotNull(result);
    assertEquals(2, result.getThreecAcceptedCardType().size());
    assertEquals("/content/dam/global/booking/google.jpg", result.getPaymentMethods().get(0).getLogoSrc());
  }

  @Test
  void googlePayWithWithoutPaymentType() {

    //Given
    final String hotelId = "GLABAL";
    RuleData ruleData = mockBuildRuleData(hotelId, "APPS_ANDROID");
    ruleData.getPaymentMethods().get(0).setType("");

    //When
    var result = googlePaymentRule.calculate(ruleData);

    //Then
    assertNotNull(result);
    assertEquals(2, result.getThreecAcceptedCardType().size());
    assertNull(result.getPaymentMethods().get(0).getLogoSrc());
  }

  // ── Datatrans tests ───────────────────────────────────────────────────────

  @Test
  void googlePay_paymentProvider_isPlanet3CP_whenDataTransDisabled() {
    //Given
    RuleData ruleData = mockBuildRuleData("GLABAL", "APPS_ANDROID");
    // no isDataTransEnabled / dataTransProperties → disabled

    //When
    var result = googlePaymentRule.calculate(ruleData);

    //Then
    result.getPaymentMethods().stream()
        .filter(pm -> "GP".equalsIgnoreCase(pm.getType()))
        .forEach(pm -> assertEquals(PaymentProviderType.PLANET_3CP, pm.getPaymentProvider()));
  }

  @Test
  void googlePay_paymentProvider_isDatatrans_whenEnabledAndGpNotExcluded() {
    //Given
    RuleData ruleData = mockBuildRuleDataWithDatatrans("GLABAL", "APPS_ANDROID",
        new DataTransConfig(List.of("SAVED_CARD"))); // GP not excluded

    //When
    var result = googlePaymentRule.calculate(ruleData);

    //Then
    result.getPaymentMethods().stream()
        .filter(pm -> "GP".equalsIgnoreCase(pm.getType()))
        .forEach(pm -> assertEquals(PaymentProviderType.DATATRANS, pm.getPaymentProvider()));
  }

  @Test
  void googlePay_paymentProvider_isPlanet3CP_whenEnabledButGpExcluded() {
    //Given
    RuleData ruleData = mockBuildRuleDataWithDatatrans("GLABAL", "APPS_ANDROID",
        new DataTransConfig(List.of("GP"))); // GP excluded

    //When
    var result = googlePaymentRule.calculate(ruleData);

    //Then
    result.getPaymentMethods().stream()
        .filter(pm -> "GP".equalsIgnoreCase(pm.getType()))
        .forEach(pm -> assertEquals(PaymentProviderType.PLANET_3CP, pm.getPaymentProvider()));
  }

  private RuleData mockBuildRuleDataWithDatatrans(String hotelId, String channel,
      DataTransConfig config) {
    return RuleData.builder()
        .threecAcceptedCardType(mockAcceptedCardTypes())
        .datatransAcceptedCardType(mockDatatransCardTypes())
        .isDataTransEnabled(true)
        .dataTransProperties(config)
        .reservation(mockReservation(hotelId))
        .paymentMethods(mockPaymentMethods())
        .paymentMethodsConfiguration(mockPaymentMethodsConfiguration())
        .clientChannel(channel)
        .build();
  }

  private List<AcceptedCardType> mockDatatransCardTypes() {
    AcceptedCardType eca = new AcceptedCardType();
    eca.setType("ECA");
    eca.setName("Mastercard (Datatrans)");

    AcceptedCardType vis = new AcceptedCardType();
    vis.setType("VIS");
    vis.setName("Visa (Datatrans)");
    return List.of(eca, vis);
  }

  private RuleData mockBuildRuleData(String hotelId, String Channel) {
    return RuleData.builder()
            .threecAcceptedCardType(mockAcceptedCardTypes())
            .reservation(mockReservation(hotelId))
            .paymentMethods(mockPaymentMethods())
            .paymentMethodsConfiguration(mockPaymentMethodsConfiguration())
            .clientChannel(Channel)
            .build();
  }

  private RuleData mockBuildRuleDataV1() {
    return RuleData.builder()
            .threecAcceptedCardType(mockAcceptedCardTypes())
            .reservation(mockReservation("DRECIT"))
            .paymentMethods(mockPaymentMethods())
            .build();
  }

  private List<AcceptedCardType> mockAcceptedCardTypes() {
    final String paymentMethodCodeGoogle = "GP";
    final String paymentMethodNameGoogle = "Google Pay";
    final String paymentMethodSchemeLogoGoogle = "/content/dam/global/booking/google.jpg";

    final String paymentMethodCodeVisa = "MC";
    final String paymentMethodNameVisa = "Mastercard Credit";
    final String paymentMethodSchemeLogoVisa = "/content/dam/global/booking/Mastercard.jpg";

    AcceptedCardType acceptedCardTypeApple = new AcceptedCardType();
    acceptedCardTypeApple.setType(paymentMethodCodeGoogle);
    acceptedCardTypeApple.setName(paymentMethodNameGoogle);
    acceptedCardTypeApple.setLogoSrc(paymentMethodSchemeLogoGoogle);

    AcceptedCardType acceptedCardTypeVisa = new AcceptedCardType();
    acceptedCardTypeVisa.setType(paymentMethodCodeVisa);
    acceptedCardTypeVisa.setName(paymentMethodNameVisa);
    acceptedCardTypeVisa.setLogoSrc(paymentMethodSchemeLogoVisa);

    return List.of(acceptedCardTypeApple, acceptedCardTypeVisa);
  }

  private List<AcceptedCardType> mockAcceptedCardTypesV1() {
    final String paymentMethodCodeVisa = "MC";
    final String paymentMethodNameVisa = "Mastercard Credit";
    final String paymentMethodSchemeLogoVisa = "/content/dam/global/booking/Mastercard.jpg";

    AcceptedCardType acceptedCardTypeVisa = new AcceptedCardType();
    acceptedCardTypeVisa.setType(paymentMethodCodeVisa);
    acceptedCardTypeVisa.setName(paymentMethodNameVisa);
    acceptedCardTypeVisa.setLogoSrc(paymentMethodSchemeLogoVisa);

    return List.of(acceptedCardTypeVisa);
  }

  private Reservation mockReservation(String hotelId) {
    return Reservation.builder()
        .hotelId(hotelId)
        .build();
  }

  private List<PaymentMethod> mockPaymentMethods() {
    PaymentMethod paymentMethodOne = new PaymentMethod();

    paymentMethodOne.setName("GOOGLE");
    paymentMethodOne.setType("GP");
    paymentMethodOne.setEnabled(true);
    paymentMethodOne.setAcceptedCardTypes(mockAcceptedCardTypes());
    paymentMethodOne.setPaymentOptions(mockPaymentOptions());

    List<PaymentMethod> paymentMethods = new ArrayList<>();
    paymentMethods.add(paymentMethodOne);
    return paymentMethods;
  }

  private List<PaymentMethodsConfiguration> mockPaymentMethodsConfiguration() {
    List<PaymentMethodsConfiguration> paymentMethodsConfigurations = new ArrayList<>();
    PaymentMethodsConfiguration pm = new PaymentMethodsConfiguration();
    pm.setName("Google Pay");
    pm.setCode("GP");
    pm.setSupportedChannels("PI,BB,APPS_ANDROID,APPS_IOS");
    pm.setSupportedBookingTypes("PAY_ON_ARRIVAL, PAY_NOW");
    pm.setSupportedCards("VC");
    pm.setLogo("/content/dam/global/booking/google.jpg");
    pm.setListOrder("4");
    paymentMethodsConfigurations.add(pm);
    return paymentMethodsConfigurations;
  }

  private List<PaymentOption> mockPaymentOptions() {
    PaymentOption paymentOptionOne = new PaymentOption();
    PaymentOption paymentOptionTwo = new PaymentOption();

    paymentOptionOne.setType("PAY_NOW");
    paymentOptionOne.setOrder(1);
    paymentOptionOne.setEnabled(true);

    paymentOptionTwo.setType("PAY_ON_ARRIVAL");
    paymentOptionTwo.setOrder(2);
    paymentOptionTwo.setEnabled(true);

    return List.of(paymentOptionOne, paymentOptionTwo);
  }
}