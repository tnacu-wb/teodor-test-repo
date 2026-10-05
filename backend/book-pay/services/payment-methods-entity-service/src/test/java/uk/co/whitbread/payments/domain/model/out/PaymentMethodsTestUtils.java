package uk.co.whitbread.payments.domain.model.out;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.payments.infrastructure.rest.controller.payment.model.out.PaymentType;

@Slf4j
public class PaymentMethodsTestUtils {

  private static final String NEW_CARD = "NEW_CARD";
  private static final String PAYPAL_TYPE = "PAYPAL";

  private static final String APPLE_PAY_TYPE = "AP";

  private static final String GOOGLE_PAY_TYPE = "GP";
  private static final String NEW_PIBA = "NEW_PIBA";
  private static final String PAY_NOW = "PAY_NOW";
  private static final String SAVED_CARD = "SAVED_CARD";
  private static final String PAY_ON_ARRIVAL = "PAY_ON_ARRIVAL";

  public static PaymentMethod getNewPibaPaymentMethod(String name, String subType) {

    var paymentMethod = new PaymentMethod();
    paymentMethod.setName(name);
    paymentMethod.setReasons(new ArrayList<>());
    paymentMethod.setType(NEW_PIBA);
    paymentMethod.setSubType(subType);
    paymentMethod.setEnabled(Boolean.TRUE);

    var paymentOption1 = new PaymentOption();
    paymentOption1.setType(PAY_NOW);
    paymentOption1.setOrder(1);
    paymentOption1.setEnabled(true);

    var paymentOption2 = new PaymentOption();
    paymentOption2.setType(PAY_ON_ARRIVAL);
    paymentOption2.setOrder(2);
    paymentOption2.setEnabled(true);

    paymentMethod.setPaymentOptions((List.of(paymentOption1, paymentOption2)));

    return paymentMethod;
  }

  public static PaymentMethod getNewCardPaymentMethod() {
    var paymentMethod = paymentMethodBuilder();

    var paymentOption1 = paymentOptionBuilder(PAY_NOW, 1, true);

    var paymentOption2 = paymentOptionBuilder(PAY_ON_ARRIVAL, 2, true);

    List<PaymentOption> paymentOptions = new ArrayList<>();
    paymentOptions.add(paymentOption1);
    paymentOptions.add(paymentOption2);

    paymentMethod.setPaymentOptions(paymentOptions);

    return paymentMethod;
  }

  private static PaymentMethod paymentMethodBuilder(){
    var paymentMethod = new PaymentMethod();
    paymentMethod.setName("New Card");
    paymentMethod.setType(NEW_CARD);
    paymentMethod.setEnabled(Boolean.TRUE);
    return paymentMethod;
  }

  private static PaymentOption paymentOptionBuilder(final String type,
                                                    final int order, final boolean isEnabled) {
    var paymentOption = new PaymentOption();
    paymentOption.setType(type);
    paymentOption.setOrder(order);
    paymentOption.setEnabled(isEnabled);
    return paymentOption;
  }

  public static PaymentMethod getNewCardPaymentMethod(final boolean isPayNowEnabled,
      final boolean isPayOnArrivalEnabled) {
    var paymentMethod = paymentMethodBuilder();

    var paymentOption1 = paymentOptionBuilder(PAY_NOW, 1, isPayNowEnabled);

    var paymentOption2 = paymentOptionBuilder(PAY_ON_ARRIVAL, 2, isPayOnArrivalEnabled);

    List<PaymentOption> paymentOptions = new ArrayList<>();
    paymentOptions.add(paymentOption1);
    paymentOptions.add(paymentOption2);

    paymentMethod.setPaymentOptions(paymentOptions);

    return paymentMethod;
  }

  public static PaymentMethod getPayOnArrivalPaymentMethod(final boolean isPayNowEnabled,
                                                      final boolean isPayOnArrivalEnabled) {
    var paymentMethod = new PaymentMethod();
    paymentMethod.setName("PIBA UK");
    paymentMethod.setType(NEW_PIBA);
    paymentMethod.setEnabled(Boolean.TRUE);

    var paymentOption1 = paymentOptionBuilder(PAY_NOW, 1, isPayNowEnabled);

    var paymentOption2 = paymentOptionBuilder(PAY_ON_ARRIVAL, 2, isPayOnArrivalEnabled);

    List<PaymentOption> paymentOptions = new ArrayList<>();
    paymentOptions.add(paymentOption1);
    paymentOptions.add(paymentOption2);

    paymentMethod.setPaymentOptions(paymentOptions);

    return paymentMethod;
  }

  public static PaymentMethod getPaypalPaymentMethod() {
    var paymentMethod = new PaymentMethod();
    paymentMethod.setName("Paypal");
    paymentMethod.setType(PAYPAL_TYPE);
    paymentMethod.setEnabled(Boolean.TRUE);

    var paymentOption1 = new PaymentOption();
    paymentOption1.setType(PAY_NOW);
    paymentOption1.setOrder(2);
    paymentOption1.setEnabled(true);

    paymentMethod.setPaymentOptions((List.of(paymentOption1)));

    return paymentMethod;
  }

  public static PaymentMethod getApplePaymentMethod() {
    var paymentMethod = new PaymentMethod();
    paymentMethod.setName("APPLE");
    paymentMethod.setType(APPLE_PAY_TYPE);
    paymentMethod.setEnabled(Boolean.TRUE);

    var paymentOption1 = new PaymentOption();
    paymentOption1.setType(PAY_NOW);
    paymentOption1.setOrder(1);
    paymentOption1.setEnabled(true);

    paymentMethod.setPaymentOptions((List.of(paymentOption1)));

    return paymentMethod;
  }

  public static PaymentMethod getGooglePaymentMethod() {
    var paymentMethod = new PaymentMethod();
    paymentMethod.setName("GOOGLE");
    paymentMethod.setType(GOOGLE_PAY_TYPE);
    paymentMethod.setEnabled(Boolean.TRUE);

    var paymentOption1 = new PaymentOption();
    paymentOption1.setType(PAY_NOW);
    paymentOption1.setOrder(1);
    paymentOption1.setEnabled(true);

    paymentMethod.setPaymentOptions((List.of(paymentOption1)));

    return paymentMethod;
  }

  public static ArrayList<PaymentMethod> paymentMethods() {
    return new ArrayList<>(
        List.of(
            paymentMethodsSavedCard(0),
            paymentMethodsSavedCard(1),
            paymentMethodsSavedCard(2),
            paymentMethodsSavedCard(3),
            paymentMethodsSavedCard(4),
            paymentMethodsSavedCard(5),
            paymentMethodsSavedCard(6),
            getNewCardPaymentMethod(),
            getNewPibaPaymentMethod("PIBA UK", "PIBAGB")
        )
    );
  }

  public static PaymentMethod paymentMethodsSavedCard(int index) {
    var paymentOption1 = new PaymentOption();
    paymentOption1.setType(PAY_NOW);
    paymentOption1.setOrder(1);
    paymentOption1.setEnabled(true);

    var paymentOption2 = new PaymentOption();
    paymentOption2.setType(PAY_ON_ARRIVAL);
    paymentOption2.setOrder(2);
    paymentOption2.setEnabled(true);

    var paymentMethod = new PaymentMethod();
    paymentMethod.setName("Saved Card");
    paymentMethod.setType(SAVED_CARD);
    paymentMethod.setEnabled(Boolean.TRUE);
    paymentMethod.setCard(getSavedCards().get(index));
    paymentMethod.setPaymentOptions((List.of(paymentOption1, paymentOption2)));

    return paymentMethod;
  }

  public static PaymentMethod paymentMethod(String type) {
    var option = new PaymentOption();
    option.setType(PaymentType.RESERVE_WITHOUT_CARD.name());
    option.setEnabled(true);

    var paymentMethod = new PaymentMethod();
    paymentMethod.setType(type);
    paymentMethod.setEnabled(true);
    paymentMethod.setPaymentOptions(List.of(option));
    return paymentMethod;
  }

  public static List<Card> getSavedCards() {
    ObjectMapper objectMapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .findAndRegisterModules()
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    try {
      return List.of(
          objectMapper
              .readValue(new File("src/test/resources/test_files/savedCard1.json"), Card.class),
          objectMapper
              .readValue(new File("src/test/resources/test_files/savedCard2.json"), Card.class),
          objectMapper
              .readValue(new File("src/test/resources/test_files/savedCard3.json"), Card.class),
          objectMapper
              .readValue(new File("src/test/resources/test_files/savedCard4.json"), Card.class),
          objectMapper
              .readValue(new File("src/test/resources/test_files/savedCard5.json"), Card.class),
          objectMapper.readValue(new File("src/test/resources/test_files/savedCard6.json"),
              Card.class),
          objectMapper.readValue(new File("src/test/resources/test_files/savedCard7.json"),
              Card.class),
          objectMapper.readValue(new File("src/test/resources/test_files/savedCard8.json"),
                      Card.class)
      );
    } catch (IOException e) {
      log.error("Error while trying to read the file.", e);
      e.printStackTrace();
    }
    return null;
  }

  public static PaymentMethod paymentMethod(CardOption cardOption,
      CardType cardType,
      StoredCardType storedCardType,
      PaymentPolicy paymentPolicy) {

    var option = new PaymentOption();
    option.setType(paymentPolicy.name());
    option.setEnabled(true);

    var card = new Card();
    card.setType(cardType.name());
    card.setCardType(storedCardType == null ? null : storedCardType.name());

    var paymentMethod = new PaymentMethod();
    paymentMethod.setType(cardOption.name());
    paymentMethod.setPaymentOptions(List.of(option));
    paymentMethod.setCard(card);
    return paymentMethod;
  }

  public static BookingAllowances bookingAllowances() {
    return new BookingAllowances.BookingAllowancesBuilder()
        .allowAlcohol(false)
        .allowCarParking(true)
        .allowAdditionalCosts(true)
        .allowPremierSaverRates(true)
        .allowIndividualCards(true)
        .maxDinnerBudgets(PriceCapLocations.builder()
            .ukWide(Price.builder()
                .amount(BigDecimal.valueOf(25))
                .currency("GBP")
                .build())
            .build()).build();
  }

}