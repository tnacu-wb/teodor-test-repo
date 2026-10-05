package uk.co.whitbread.content.infrastructure.rest.client.aem.model.apps.homepage.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppsHomepageCardDto {

  private String imagePath;
  private String imageTag;
  private String title;
  private String subtitle;
  private String linkPath;
  private String trackingId;
  private Boolean openLinkInApp;
  private Integer order;
  private String latitude;
  private String longitude;
}