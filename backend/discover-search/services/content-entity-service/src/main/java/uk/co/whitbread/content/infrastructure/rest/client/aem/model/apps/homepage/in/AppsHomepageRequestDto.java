package uk.co.whitbread.content.infrastructure.rest.client.aem.model.apps.homepage.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppsHomepageRequestDto {

  private String country;
  private String language;
  private String channel;
  private String subchannel;
}