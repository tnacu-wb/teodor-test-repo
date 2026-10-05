package uk.co.whitbread.content.infrastructure.rest.client.aem.model.apps.homepage.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppsHomepageResponseDto {

  private AppsHomepageLogoDto logo;
  private AppsHomepageHeadingDto heading;
  private List<AppsHomepageCardDto> destinationCards;
  private List<AppsHomepageCardDto> contentCards;
  private List<AppsHomepageCardDto> promoCards;
  private AppsNotificationDto notification;
}