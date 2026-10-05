package uk.co.whitbread.content.domain.ports.secondary;

import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.in.CardManagementRequest;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.in.CommonIconsRequest;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out.CardManagementResponse;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out.CommonIcons;

public interface CardManagementOutPort {

  CardManagementResponse getCardManagementInfo(CardManagementRequest cardManagementRequest);

  CommonIcons getCommonIconsInfo(CommonIconsRequest commonIconsRequest);

}
