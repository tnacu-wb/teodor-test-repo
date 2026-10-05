package uk.co.whitbread.content.domain.model.apps.homepage.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AppsHomepageResponse {

  private AppsHomepageLogo logo;
  private AppsHomepageHeading heading;
  private List<AppsHomepageDestinationCard> destinationCards;
  private List<AppsHomepageContentCard> contentCards;
  private List<AppsHomepageCard> promoCards;
  private AppsNotification notification;
}