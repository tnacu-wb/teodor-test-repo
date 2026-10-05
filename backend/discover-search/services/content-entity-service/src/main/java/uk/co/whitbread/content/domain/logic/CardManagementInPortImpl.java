package uk.co.whitbread.content.domain.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.in.CardManagementRequest;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.in.CommonIconsRequest;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out.CardManagementResponse;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out.CommonIcons;
import uk.co.whitbread.content.domain.ports.primary.CardManagementInPort;
import uk.co.whitbread.content.domain.ports.secondary.CardManagementOutPort;

@Slf4j
@RequiredArgsConstructor
public class CardManagementInPortImpl implements CardManagementInPort {

  private final CardManagementOutPort cardManagementOutPort;

  @Override
  public CardManagementResponse getCardManagementInfo(CardManagementRequest cardManagementRequest) {
    return cardManagementOutPort.getCardManagementInfo(cardManagementRequest);
  }

  @Override
  public CommonIcons getCommonIconsInfo(CommonIconsRequest commonIconsRequest) {
    return cardManagementOutPort.getCommonIconsInfo(commonIconsRequest);
  }
}
