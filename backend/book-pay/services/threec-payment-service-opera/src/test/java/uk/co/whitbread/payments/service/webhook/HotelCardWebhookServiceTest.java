package uk.co.whitbread.payments.service.webhook;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.client.HotelCardClient;
import uk.co.whitbread.payments.mapper.PaymentCardMapperImpl;
import uk.co.whitbread.payments.model.Address;
import uk.co.whitbread.payments.model.PaymentResponse;
import uk.co.whitbread.payments.model.ProviderResponse;
import uk.co.whitbread.payments.model.SaveCardDetails;
import uk.co.whitbread.payments.model.ThreeCResponse;
import uk.co.whitbread.payments.model.card.AddressDTO;
import uk.co.whitbread.payments.model.card.PaymentCardDTO;

@ExtendWith(MockitoExtension.class)
class HotelCardWebhookServiceTest {

  private static final String CARD_ID = "VISA";
  private static final String CARD_LABEL = "my card";
  private static final String USER_EMAIL = "myEmail@yahoo.com";
  private static final String LINE_1 = "my addr";
  private static final String POST_CODE = "A2B 2C";
  private static final String EXPIRY_DATE = "1227";
  private static final String CARD_TOKEN = "123456789";
  private static final String CARD_NUMBER_LAST_4_DIGITS = "1234";
  private static final String FIRST_NAME = "First";
  private static final String LAST_NAME = "Last";
  private static final String COUNTRY_CODE = "GB";
  private static final String CARD_TYPE_PIBA = "PIBA";

  @Mock
  private HotelCardClient hotelCardClient;

  private HotelCardWebhookService hotelCardWebhookService;

  @BeforeEach
  void init() {
    hotelCardWebhookService = new HotelCardWebhookService(hotelCardClient, new PaymentCardMapperImpl());
  }


  @ParameterizedTest
  @CsvSource({"PI,PIBA", "VS,VISA"})
  void saveOrUpdateCard(String schemaId, String schemaName) {

    var paymentCardDTO = PaymentCardDTO.builder()
        .cardId(CARD_ID)
        .cardLabel(CARD_LABEL)
        .expiryDate(EXPIRY_DATE)
        .personalCard(Boolean.FALSE)
        .business(Boolean.TRUE)
        .cnpRequired(Boolean.FALSE)
        .userEmail(USER_EMAIL)
        .cardType(schemaId)
        .cardToken(CARD_TOKEN)
        .cardNumberLast4Digits(CARD_NUMBER_LAST_4_DIGITS)
        .cardHolderName(FIRST_NAME + " " + LAST_NAME)
        .billingAddress(AddressDTO.builder()
            .line1(LINE_1)
            .postCode(POST_CODE)
            .countryCode(COUNTRY_CODE)
            .build())
        .build();

    var encoder = Base64.getEncoder();
    var lastName = new String(encoder.encode(LAST_NAME.getBytes(UTF_8)));
    var firstName = new String(encoder.encode(FIRST_NAME.getBytes(UTF_8)));
    var name = new String(encoder.encode((FIRST_NAME + " " + LAST_NAME).getBytes(UTF_8)));

    var response = PaymentResponse.builder()
        .saveCardDetails(SaveCardDetails.builder()
            .cardId(CARD_ID)
            .cardLabel(CARD_LABEL)
            .personalCard(Boolean.FALSE)
            .business(Boolean.TRUE)
            .cnpRequired(Boolean.FALSE)
            .email(USER_EMAIL)
            .billingAddress(Address.builder()
                .line1(LINE_1)
                .postalCode(POST_CODE)
                .countryCode(COUNTRY_CODE)
                .build())
            .build())
        .providerResponse(ProviderResponse.builder()
            .threeCResponse(ThreeCResponse.builder()
                .cardSchemeId(schemaId)
                .cardSchemeName(schemaName)
                .token(CARD_TOKEN)
                .last4Digits(CARD_NUMBER_LAST_4_DIGITS)
                .expiry(EXPIRY_DATE)
                .cardholderFirstName(CARD_TYPE_PIBA.equals(schemaName) ? name : firstName)
                .cardholderLastName(lastName)
                .build())
            .build())
        .build();

    when(hotelCardClient.saveOrUpdateCard(paymentCardDTO)).thenReturn(Mono.just(new ResponseEntity(HttpStatus.OK)));

    var actualResponse = hotelCardWebhookService.saveOrUpdateCard(response).block();

    assertNotNull(actualResponse);
  }
}