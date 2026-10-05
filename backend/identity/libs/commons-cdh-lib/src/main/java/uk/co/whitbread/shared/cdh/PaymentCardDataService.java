package uk.co.whitbread.shared.cdh;

import static uk.co.whitbread.shared.cdh.properties.UriPaths.ACCOUNT_SERVICES_ENDPOINT;
import static uk.co.whitbread.shared.cdh.properties.UriPaths.CARD;
import static uk.co.whitbread.shared.cdh.properties.UriPaths.CARDS;
import static uk.co.whitbread.shared.cdh.properties.UriPaths.COMPANY;

import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import uk.co.whitbread.shared.cdh.model.card.GetPaymentCardDetailsResponse;
import uk.co.whitbread.shared.cdh.model.card.PaymentCardDetails;
import uk.co.whitbread.shared.cdh.model.card.UpdatePaymentCardDetailsResponse;
import uk.co.whitbread.shared.cdh.properties.CdhApiProperties;
import uk.co.whitbread.shared.cdh.properties.CdhApiOauthProperties;

import static uk.co.whitbread.shared.cdh.utils.RequestUtils.buildHeaders;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentCardDataService {

  private final CustomerDataHubClient cdhClient;
  private final CdhApiProperties cdhApiProperties;
  private final CdhApiOauthProperties cdhApiOauthProperties;

  public List<GetPaymentCardDetailsResponse> getPaymentCards(String companyAccountId, String accessedBy) {
    final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + ACCOUNT_SERVICES_ENDPOINT + COMPANY + CARDS);
    return cdhClient.getListCDH(
        builder.buildAndExpand(companyAccountId).toUriString(),
        buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy),
        GetPaymentCardDetailsResponse.class);
  }

  @CacheEvict(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1HourCdh",
      value = "CdhCompany", key = "#companyAccountId")
  public Void deletePaymentCard(String companyAccountId,
      String cardId, String accessedBy) {
    final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + ACCOUNT_SERVICES_ENDPOINT + COMPANY + CARD);
    return cdhClient.deleteCDH(
        builder.buildAndExpand(companyAccountId, cardId).toUriString(),
        buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy), Void.class);
  }

  @CacheEvict(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1HourCdh",
      value = "CdhCompany", key = "#companyAccountId")
  public UpdatePaymentCardDetailsResponse updatePaymentCard(String companyAccountId,
      String cardId, PaymentCardDetails paymentCardRequest, String accessedBy) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + ACCOUNT_SERVICES_ENDPOINT + COMPANY + CARD);
    return cdhClient.putCDH(builder.buildAndExpand(companyAccountId, cardId).toUriString(),
        paymentCardRequest,
        buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy),
        PaymentCardDetails.class, UpdatePaymentCardDetailsResponse.class);
  }

  @CacheEvict(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1HourCdh",
      value = "CdhCompany", key = "#companyAccountId")
  public GetPaymentCardDetailsResponse createCompanyPaymentCard(String companyAccountId,
      PaymentCardDetails paymentCardDetails, String accessedBy) {
    final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + ACCOUNT_SERVICES_ENDPOINT + COMPANY + CARDS);
    return cdhClient.postCDH(builder.buildAndExpand(companyAccountId).toUriString(),
        paymentCardDetails,
        buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy),
        PaymentCardDetails.class, GetPaymentCardDetailsResponse.class);
  }
}
