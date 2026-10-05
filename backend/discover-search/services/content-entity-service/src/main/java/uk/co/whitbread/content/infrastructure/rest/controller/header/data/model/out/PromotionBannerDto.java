package uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionBannerDto {

  private Boolean enabled;
  private String icon;
  private String title;
  private String description;
  private String terms;
  private String srpNotificationTitle;
  private String srpNotificationText;

}
