package uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.in.CardManagementRequest;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.in.CommonIconsRequest;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out.CardManagementContent;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out.CardManagementResponse;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out.CommonIcons;
import uk.co.whitbread.content.domain.ports.secondary.CardManagementOutPort;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.adapter.CardManagementAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.mapper.CardManagementRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.mapper.CardManagementResponseMapper;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.mapper.CommonIconsRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.mapper.CommonIconsResponseMapper;

@Slf4j
@RequiredArgsConstructor
public class CardManagementOutPortImpl implements CardManagementOutPort {

  private final CardManagementAemClient aemClient;
  private final CommonIconsRequestMapper commonIconsRequestMapper;
  private final CommonIconsResponseMapper commonIconsResponseMapper;
  private final CardManagementRequestMapper cardManagementRequestMapper;
  private final CardManagementResponseMapper cardManagementResponseMapper;

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Day",
      value = "InnbCardManagementCache")
  @Override
  public CardManagementResponse getCardManagementInfo(CardManagementRequest cardManagementRequest) {

    var commonIconsRequest = new CommonIconsRequest(cardManagementRequest.getLanguage());
    CommonIcons commonIcons = getCommonIconsInfo(commonIconsRequest);

    var cardManagementContentRequest = new CardManagementRequest(cardManagementRequest.getLanguage());
    CardManagementContent cardManagementContent = cardManagementResponseMapper.toModel(
        aemClient.getCardManagementContentInformation(
            cardManagementRequestMapper.toDto(cardManagementContentRequest)));

    return new CardManagementResponse(cardManagementContent, commonIcons);
  }

  @Override
  public CommonIcons getCommonIconsInfo(CommonIconsRequest commonIconsRequest) {

    var commonIconsContentRequest = new CommonIconsRequest(commonIconsRequest.getLanguage());
    return commonIconsResponseMapper.toModel(aemClient.getCommonIconsInformation(
        commonIconsRequestMapper.toDto(commonIconsContentRequest)));
  }
}
