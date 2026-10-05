package uk.co.whitbread.basket.infrastructure.queue.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.basket.out.BasketItem;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentsConfirmation;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PaymentAuthorisedEventPaymentResponseMapperImpl.class})
class PaymentAuthorisedEventPaymentResponseMapperTest {

  @Autowired
  private PaymentAuthorisedEventPaymentResponseMapper mapper;

  @Test
  void shouldMapAllFieldsFromPaymentsConfirmation() {
    var confirmation = PaymentsConfirmation.builder()
        .paymentId("pay-123")
        .paymentStatus("AUTHORIZED")
        .bookingReference("BOOK-456")
        .channel("WEB")
        .language("en")
        .cardSchemeId("VS")
        .last4Digits("4242")
        .token("tok_abc")
        .expiry("12/28")
        .fraudCheckDecision("ACCEPT")
        .firstName("Sm1G5Xu=")
        .lastName("ZG9l")
        .threeDSIndicator("Y")
        .build();

    var result = mapper.toPaymentsResponseModel(confirmation, null);

    assertNotNull(result);
    assertEquals("pay-123", result.getPaymentId());
    assertEquals("AUTHORIZED", result.getPaymentStatus());
    assertEquals("BOOK-456", result.getBooking().getReference());
    assertEquals("WEB", result.getBooking().getChannel());
    assertEquals("en", result.getBooking().getLanguage());
    assertEquals("VS", result.getProviderResponse().getThreecResponse().getCardSchemeId());
    assertEquals("4242", result.getProviderResponse().getThreecResponse().getLast4Digits());
    assertEquals("tok_abc", result.getProviderResponse().getThreecResponse().getToken());
    assertEquals("12/28", result.getProviderResponse().getThreecResponse().getExpiry());
    assertEquals("ACCEPT", result.getProviderResponse().getThreecResponse().getFraudCheckDecision());
    assertEquals("Sm1G5Xu=", result.getPayment().getBilling().getFirstName());
    assertEquals("ZG9l", result.getPayment().getBilling().getLastName());
    assertEquals("Y", result.getProviderResponse().getThreecResponse().getThreeDSIndicator());
  }

  @Test
  void shouldUseLanguageFromConfirmationWhenNotBlank() {
    var confirmation = PaymentsConfirmation.builder()
        .paymentId("pay-123")
        .language("en")
        .build();
    var basket = basketWithItemLanguage("language", "de");

    var result = mapper.toPaymentsResponseModel(confirmation, basket);

    assertEquals("en", result.getBooking().getLanguage());
  }

  @Test
  void shouldFallbackToBasketItemLanguageKeyWhenConfirmationLanguageIsBlank() {
    var confirmation = PaymentsConfirmation.builder()
        .paymentId("pay-123")
        .language(null)
        .build();
    var basket = basketWithItemLanguage("language", "en");

    var result = mapper.toPaymentsResponseModel(confirmation, basket);

    assertEquals("en", result.getBooking().getLanguage());
  }

  @Test
  void shouldFallbackToBasketItemLANGUAGEKeyWhenLowercaseNotPresent() {
    var confirmation = PaymentsConfirmation.builder()
        .paymentId("pay-123")
        .language(null)
        .build();
    var basket = basketWithItemLanguage("LANGUAGE", "de");

    var result = mapper.toPaymentsResponseModel(confirmation, basket);

    assertEquals("de", result.getBooking().getLanguage());
  }

  @Test
  void shouldFallbackToBasketItemLangKeyWhenOthersNotPresent() {
    var confirmation = PaymentsConfirmation.builder()
        .paymentId("pay-123")
        .language(null)
        .build();
    var basket = basketWithItemLanguage("lang", "en");

    var result = mapper.toPaymentsResponseModel(confirmation, basket);

    assertEquals("en", result.getBooking().getLanguage());
  }

  @Test
  void shouldReturnNullLanguageWhenConfirmationBlankAndBasketIsNull() {
    var confirmation = PaymentsConfirmation.builder()
        .paymentId("pay-123")
        .language(null)
        .build();

    var result = mapper.toPaymentsResponseModel(confirmation, null);

    assertNull(result.getBooking().getLanguage());
  }

  @Test
  void shouldReturnNullLanguageWhenConfirmationBlankAndBasketItemsIsNull() {
    var confirmation = PaymentsConfirmation.builder()
        .paymentId("pay-123")
        .language(null)
        .build();
    var basket = Basket.builder().build();

    var result = mapper.toPaymentsResponseModel(confirmation, basket);

    assertNull(result.getBooking().getLanguage());
  }

  @Test
  void shouldReturnNullLanguageWhenConfirmationBlankAndNoLanguageKeyInItemDetails() {
    var confirmation = PaymentsConfirmation.builder()
        .paymentId("pay-123")
        .language(null)
        .build();
    var details = Map.of("someOtherKey", "someValue");
    var basket = basketWithItemLanguage(details);

    var result = mapper.toPaymentsResponseModel(confirmation, basket);

    assertNull(result.getBooking().getLanguage());
  }

  @Test
  void shouldSkipItemsWithNullDetailsWithoutNPE() {
    var confirmation = PaymentsConfirmation.builder()
        .paymentId("pay-123")
        .language(null)
        .build();
    var itemWithNullDetails = BasketItem.builder().type("STAY").sourceId("src1").details(null).build();
    var itemWithLanguage = BasketItem.builder().type("STAY").sourceId("src2")
        .details(Map.of("language", "en")).build();
    var basket = Basket.builder()
        .items(List.of(itemWithNullDetails, itemWithLanguage))
        .build();

    var result = mapper.toPaymentsResponseModel(confirmation, basket);

    assertEquals("en", result.getBooking().getLanguage());
  }

  @Test
  void shouldReturnNullLanguageWhenBasketItemsIsEmpty() {
    var confirmation = PaymentsConfirmation.builder()
        .paymentId("pay-123")
        .language(null)
        .build();
    var basket = Basket.builder().items(Collections.emptyList()).build();

    var result = mapper.toPaymentsResponseModel(confirmation, basket);

    assertNull(result.getBooking().getLanguage());
  }

  private Basket basketWithItemLanguage(String key, String value) {
    var details = new HashMap<String, String>();
    details.put(key, value);
    return basketWithItemLanguage(details);
  }

  private Basket basketWithItemLanguage(Map<String, String> details) {
    var item = BasketItem.builder()
        .type("STAY")
        .sourceId("src1")
        .details(details)
        .build();
    return Basket.builder()
        .items(List.of(item))
        .build();
  }
}
