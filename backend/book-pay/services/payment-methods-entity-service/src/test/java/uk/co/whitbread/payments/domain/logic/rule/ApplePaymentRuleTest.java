package uk.co.whitbread.payments.domain.logic.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;
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
class ApplePaymentRuleTest {

  private ApplePaymentRule applePaymentRule;

  @BeforeEach
  void init() {
    this.applePaymentRule = new ApplePaymentRule();
  }

  @Test
  void hotelIsAllowedToAcceptApplePay() {

    //Given
    final String hotelId = "DRECIT";

    //When
    var result = applePaymentRule.calculate(mockBuildRuleData(hotelId, "APPS_IOS"));

    //Then
    assertNotNull(result);
    assertEquals(2, result.getThreecAcceptedCardType().size());
  }

  @Test
  void hotelIsAllowedToAcceptApplePayForPI() {

    //Given
    final String hotelId = "DRECIT";

    //When
    var result = applePaymentRule.calculate(mockBuildRuleData(hotelId, "PI"));

    //Then
    assertNotNull(result);
    assertEquals(2, result.getThreecAcceptedCardType().size());
  }

  @Test
  void hotelIsAllowedToAcceptApplePayForBB() {

    //Given
    final String hotelId = "DRECIT";

    //When
    var result = applePaymentRule.calculate(mockBuildRuleData(hotelId, "BB"));

    //Then
    assertNotNull(result);
    assertEquals(2, result.getThreecAcceptedCardType().size());
  }

  @Test
  void hotelIsAllowedToAcceptApplePayWithLogoSrc() {

    //Given
    final String hotelId = "DRECIT";
    RuleData ruleData = mockBuildRuleData(hotelId, "APPS_IOS");

    //When
    var result = applePaymentRule.calculate(ruleData);

    //Then
    assertNotNull(result);
    assertEquals(2, result.getThreecAcceptedCardType().size());
    assertEquals("/content/dam/global/booking/apple.jpg", result.getPaymentMethods().get(0).getLogoSrc());
  }

  @Test
  void applePaymentRule_negative_scenario1() {

    //Given
    final String hotelId = "DRECIT";
    RuleData ruleData = mockBuildRuleData(hotelId, "APPS_IOS");
    ruleData.setThreecAcceptedCardType(mockAcceptedCardTypesV1());

    //When
    var result = applePaymentRule.calculate(ruleData);
    var testResult = result.getPaymentMethods().stream()
        .filter(test -> test.getType().equalsIgnoreCase("AP"))
        .toList();

    //Then
    assertNotNull(result);
    assertEquals(0, testResult.get(0).getAcceptedCardTypes().size());
  }

  @Test
  void applePayWithWithoutPaymentType() {

    //Given
    final String hotelId = "DRECIT";
    RuleData ruleData = mockBuildRuleData(hotelId, "APPS_IOS");
    ruleData.getPaymentMethods().get(0).setType("");

    //When
    var result = applePaymentRule.calculate(ruleData);

    //Then
    assertNotNull(result);
    assertEquals(2, result.getThreecAcceptedCardType().size());
    assertNull(result.getPaymentMethods().get(0).getLogoSrc());
  }

  // ── Datatrans tests ───────────────────────────────────────────────────────

  @Test
  void applePay_paymentProvider_isPlanet3CP_whenDataTransDisabled() {
    //Given
    RuleData ruleData = mockBuildRuleData("DRECIT", "APPS_IOS");
    // no isDataTransEnabled / dataTransProperties → disabled

    //When
    var result = applePaymentRule.calculate(ruleData);

    //Then
    result.getPaymentMethods().stream()
        .filter(pm -> "AP".equalsIgnoreCase(pm.getType()))
        .forEach(pm -> assertEquals(PaymentProviderType.PLANET_3CP, pm.getPaymentProvider()));
  }

  @Test
  void applePay_paymentProvider_isDatatrans_whenEnabledAndApNotExcluded() {
    //Given
    RuleData ruleData = mockBuildRuleDataWithDatatrans("DRECIT", "APPS_IOS",
        new DataTransConfig(List.of("SAVED_CARD"))); // AP not excluded

    //When
    var result = applePaymentRule.calculate(ruleData);

    //Then
    result.getPaymentMethods().stream()
        .filter(pm -> "AP".equalsIgnoreCase(pm.getType()))
        .forEach(pm -> assertEquals(PaymentProviderType.DATATRANS, pm.getPaymentProvider()));
  }

  @Test
  void applePay_paymentProvider_isPlanet3CP_whenEnabledButApExcluded() {
    //Given
    RuleData ruleData = mockBuildRuleDataWithDatatrans("DRECIT", "APPS_IOS",
        new DataTransConfig(List.of("AP"))); // AP excluded

    //When
    var result = applePaymentRule.calculate(ruleData);

    //Then
    result.getPaymentMethods().stream()
        .filter(pm -> "AP".equalsIgnoreCase(pm.getType()))
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
        .paymentMethodsConfiguration(mockPaymentMethodsConfiguration())
        .paymentMethods(mockPaymentMethods())
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
        .paymentMethodsConfiguration(mockPaymentMethodsConfiguration())
        .paymentMethods(mockPaymentMethods())
        .clientChannel(Channel)
        .build();
  }

  private List<PaymentMethodsConfiguration> mockPaymentMethodsConfiguration() {
    List<PaymentMethodsConfiguration> paymentMethodsConfigurations = new ArrayList<>();
    PaymentMethodsConfiguration pm = new PaymentMethodsConfiguration();
    pm.setName("Apple Pay");
    pm.setCode("AP");
    pm.setSupportedChannels("PI,BB,APPS_IOS");
    pm.setSupportedBookingTypes("PAY_ON_ARRIVAL, PAY_NOW");
    pm.setSupportedCards("MC");
    pm.setLogo("/content/dam/global/booking/apple.jpg");
    pm.setListOrder("5");
    paymentMethodsConfigurations.add(pm);
    return paymentMethodsConfigurations;
  }

  private List<AcceptedCardType> mockAcceptedCardTypes() {
    final String paymentMethodCodeApple = "AP";
    final String paymentMethodNameApple = "Apple Pay";
    final String paymentMethodSchemeLogoApple = "/content/dam/global/booking/apple.jpg";

    final String paymentMethodCodeVisa = "VS";
    final String paymentMethodNameVisa = "Visa Credit";
    final String paymentMethodSchemeLogoVisa = "/content/dam/global/booking/VC.jpg";

    AcceptedCardType acceptedCardTypeApple = new AcceptedCardType();
    acceptedCardTypeApple.setType(paymentMethodCodeApple);
    acceptedCardTypeApple.setName(paymentMethodNameApple);
    acceptedCardTypeApple.setLogoSrc(paymentMethodSchemeLogoApple);

    AcceptedCardType acceptedCardTypeVisa = new AcceptedCardType();
    acceptedCardTypeVisa.setType(paymentMethodCodeVisa);
    acceptedCardTypeVisa.setName(paymentMethodNameVisa);
    acceptedCardTypeVisa.setLogoSrc(paymentMethodSchemeLogoVisa);

    return List.of(acceptedCardTypeApple, acceptedCardTypeVisa);
  }

  private List<AcceptedCardType> mockAcceptedCardTypesV1() {
    final String paymentMethodCodeVisa = "VS";
    final String paymentMethodNameVisa = "Visa Credit";
    final String paymentMethodSchemeLogoVisa = "/content/dam/global/booking/VC.jpg";

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
    paymentMethodOne.setName("APPLE");
    paymentMethodOne.setType("AP");
    paymentMethodOne.setEnabled(true);
    paymentMethodOne.setAcceptedCardTypes(mockAcceptedCardTypes());
    paymentMethodOne.setPaymentOptions(mockPaymentOptions());

    List<PaymentMethod> paymentMethods = new ArrayList<>();
    paymentMethods.add(paymentMethodOne);
    return paymentMethods;
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