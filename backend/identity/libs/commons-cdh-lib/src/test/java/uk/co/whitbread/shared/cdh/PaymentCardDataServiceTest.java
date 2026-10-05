package uk.co.whitbread.shared.cdh;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpHeaders;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.shared.cdh.model.card.GetPaymentCardDetailsResponse;
import uk.co.whitbread.shared.cdh.model.card.PaymentCardDetails;
import uk.co.whitbread.shared.cdh.model.card.UpdatePaymentCardDetailsResponse;
import uk.co.whitbread.shared.cdh.properties.CdhApiOauthProperties;
import uk.co.whitbread.shared.cdh.properties.CdhApiProperties;
import uk.co.whitbread.shared.cdh.properties.UriPaths;

@ExtendWith(MockitoExtension.class)
public class PaymentCardDataServiceTest {

  private static final String COMPANY_ACCOUNT_ID = "COMP5f2e7e80-b4fd-42e9-958a-b9b24b61a9d5";
  private static final String CARD_ID = "CARD97aa5ff0-038a-4260-bcfd-cbf8b20f236a";
  private static final String ACCESSED_BY = "customer@mail.com";
  private static final String HOST = "https://localhost";
  private static final String SUBSCRIPTION_KEY = "subscription-key";
  private ObjectMapper objectMapper = new ObjectMapper();

  @Mock
  private CdhApiProperties cdhApiProperties;
  @Mock
  private CdhApiOauthProperties cdhApiOauthProperties;
  @Mock
  private CustomerDataHubClient cdhClient;
  @InjectMocks
  private PaymentCardDataService paymentCardDataService;

  @BeforeEach
  public void setup() {
    when(cdhApiProperties.getHost()).thenReturn(HOST);
    when(cdhApiOauthProperties.getSubscriptionKey()).thenReturn(SUBSCRIPTION_KEY);
  }

  @Test
  public void getPaymentCards_success() throws IOException {
    when(cdhClient.getListCDH(anyString(), any(HttpHeaders.class),
        eq(GetPaymentCardDetailsResponse.class)))
        .thenReturn(List.of(buildGetPaymentCardResponse()));

    var paymentCards = paymentCardDataService.getPaymentCards(COMPANY_ACCOUNT_ID, ACCESSED_BY);
    assertEquals(paymentCards.get(0).getCardId(), CARD_ID);
  }

  @Test
  public void getPaymentCard_urlIsBuiltCorrectly() {
    final var urlCaptor = ArgumentCaptor.forClass(String.class);
    paymentCardDataService.getPaymentCards(COMPANY_ACCOUNT_ID, ACCESSED_BY);

    verify(cdhClient).getListCDH(urlCaptor.capture(),
        any(HttpHeaders.class),
        eq(GetPaymentCardDetailsResponse.class));

    final var url = urlCaptor.getValue();

    final var expectedUrl = cdhApiProperties.getHost()
        + UriPaths.ACCOUNT_SERVICES_ENDPOINT
        + UriPaths.COMPANIES + "/" + COMPANY_ACCOUNT_ID
        + UriPaths.CARDS;

    assertThat(url, is(expectedUrl));
  }

  @Test
  public void deletePayment_success() {
    when(cdhClient.deleteCDH(anyString(),
        any(HttpHeaders.class),
        eq(Void.class))).thenReturn(null);
    final var response = paymentCardDataService.deletePaymentCard(COMPANY_ACCOUNT_ID,
        CARD_ID, ACCESSED_BY);
    assertThat(response, nullValue());
  }

  @Test
  public void deletePaymentCard_urlIsBuiltCorrectly() {
    paymentCardDataService.deletePaymentCard(COMPANY_ACCOUNT_ID, CARD_ID, ACCESSED_BY);

    final var urlCaptor = ArgumentCaptor.forClass(String.class);
    verify(cdhClient).deleteCDH(urlCaptor.capture(),
        any(HttpHeaders.class), eq(Void.class));

    final var url = urlCaptor.getValue();

    final var expectedUrl = cdhApiProperties.getHost()
        + UriPaths.ACCOUNT_SERVICES_ENDPOINT
        + UriPaths.COMPANIES + "/" + COMPANY_ACCOUNT_ID
        + UriPaths.CARDS + "/" + CARD_ID;

    assertThat(url, is(expectedUrl));
  }

  @Test
  public void updatePaymentCard_success() throws IOException {
    var updateCardResponse = UpdatePaymentCardDetailsResponse.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID)
        .cardId(CARD_ID).build();
    when(cdhClient.putCDH(any(), any(), any(), any(), any())).thenReturn(
        updateCardResponse);

    final var response = paymentCardDataService.updatePaymentCard(
        COMPANY_ACCOUNT_ID, CARD_ID, buildUpdatePaymentCardDetailsRequest(), ACCESSED_BY);

    assertNotNull(response);
    assertThat(response.getCompanyAccountId(), is(COMPANY_ACCOUNT_ID));
    assertThat(response.getCardId(), is(CARD_ID));
  }

  @Test
  public void addPaymentCard_success() throws IOException {
    var createdCardResponse = buildGetPaymentCardResponse();
    when(cdhClient.postCDH(any(), any(), any(), any(), any())).thenReturn(
        createdCardResponse);

    final var response = paymentCardDataService.createCompanyPaymentCard(
        COMPANY_ACCOUNT_ID, buildUpdatePaymentCardDetailsRequest(), ACCESSED_BY);

    assertNotNull(response);
  }

  private GetPaymentCardDetailsResponse buildGetPaymentCardResponse() throws IOException {
    return objectMapper.readValue(
        new File("src/test/resources/samples/get_paymentCard_response.json"),
        GetPaymentCardDetailsResponse.class);
  }

  private PaymentCardDetails buildUpdatePaymentCardDetailsRequest()
      throws IOException {
    return objectMapper.readValue(
        new File("src/test/resources/samples/paymentCardDetails_request.json"),
        PaymentCardDetails.class);
  }
}
