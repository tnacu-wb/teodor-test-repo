package uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionBanner {

  private Boolean enabled;
  private String icon;
  private String title;
  private String description;
  private String terms;
  private String srpNotificationTitle;
  private String srpNotificationText;
}
