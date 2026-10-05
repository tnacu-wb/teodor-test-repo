package uk.co.whitbread.content.infrastructure.rest.client.aem.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.aem")
public class AemProperties {

  private String host;
  private String hostMock;
  private String username;
  private String password;
  private Boolean isProduction = false;
  private String labelsEndpoint;
  private String indexHeaderDataEndpoint;
  private String searchResultsDataEndpoint;
  private String amendLabelsEndpoint;
  private String bookingLabelsEndpoint;
  private String businessNotesEndpoint;
  private String hotelInformationEndpoint;
  private String bookingInformationEndpoint;
  private String upsellItemsEndpoint;
  private String allHotelDetailsEndpoint;
  private String rateInformationEndpoint;
  private String bbRateInformationEndpoint;
  private String rateInformationForHotelEndpoint;
  private String bbRateInformationForHotelEndpoint;
  private String footerEndpoint;
  private String roomTypeEndpoint;
  private String countriesEndpoint;
  private String cookiePoliciesEndpoint;
  private String bbIndexHeaderDataEndpoint;
  private String ratesOverrideEndpoint;
  private String bbRatesOverrideEndpoint;
  private String piPreCheckInEndpoint;
  private String piGroupBookingEndpoint;
  private String innbContentEndpoint;
  private String innbLayoutEndpoint;
  private String innbCardManagementEndpoint;
  private String innbCommonIconsEndpoint;
  private String extrasLabelsEndpoint;
  private String innbCommonLayoutEndpoint;
  private String innbCompanyManagementEndpoint;
  private String piGlobalConfigEndpoint;
  private String bbGlobalConfigEndpoint;
  private String ccuiGlobalConfigEndpoint;
  private String distrGlobalConfigEndpoint;
  private String innbUserManagementEndpoint;
  private String innbProfileManagementEndpoint;
  private String pageDlpEndpoint;
  private String homePageEndpoint;
  private String spendingReportingEndpoint;
  private String appsHomepageEndPoint;
  private String payApplicationEndpoint;
  private String authEndpoint;
  private String notificationsEndpoint;
  private String innbContactUsEndpoint;
  private String promoLabelsEndpoint;
}
