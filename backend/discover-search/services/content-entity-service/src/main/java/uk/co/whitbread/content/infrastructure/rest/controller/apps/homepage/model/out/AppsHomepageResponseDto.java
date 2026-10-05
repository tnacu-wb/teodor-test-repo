package uk.co.whitbread.content.infrastructure.rest.controller.apps.homepage.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppsHomepageResponseDto {

  private AppsHomepageLogoDto logo;
  private String heading;
  private List<AppsHomepageDestinationCardDto> destinationCards;
  private List<AppsHomepageContentCardDto> contentCards;
  private List<AppsHomepageCardDto> promoCards;
  private AppsNotificationDto notification;
}