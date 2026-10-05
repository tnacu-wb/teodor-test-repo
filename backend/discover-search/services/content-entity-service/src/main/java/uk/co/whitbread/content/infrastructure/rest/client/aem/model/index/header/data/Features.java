package uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Features {

  private Boolean landingPagesMenu;
  private Boolean analytics;
  private Boolean bookingSearch;
  private Boolean dashboardRedirectEnabled;
  private Boolean subNav;
  private Boolean enableCookieModal;
  private Boolean announcement;
  private Boolean authentication;
  private Boolean languageSelector;
  private Boolean businessAccountLink;
  private Boolean businessSubNav;
  private Boolean hotelSearch;
}
