package uk.co.whitbread.content.infrastructure.rest.client.aem.model.apps.homepage.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppsNotificationDto {
  private String type;
  private String title;
  private String message;
  private String linkLabel;
  private String linkPath;
  private Boolean openLinkInApp;
  private Boolean dismissible;
}