package uk.co.whitbread.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.ohip")
public class OhipProperties {

  private String host;
  private String availabilitiesEndpoint;
  private String availabilityByIdsEndpoint;
  private String availabilityByIdsEndpointV2;
  private String multiHotelAvailabilitiesEndpoint;
  private String multiHotelAvailabilitiesEndpoint2;
  private String packagesEndpoint;
  private String donationPackagesEndpoint;
  private String rateCodePricingEndpoint;
  private String roomPriceBreakdownEndpoint;
  private String hotelInventoryEndpoint;
  private String itemInventoryEndpoint;
  private String hotelInfoEndpoint;
  private String roomTypesInfoEndpoint;
  private String cancellationReasonsEndpoint;
  private String ratePlansEndpoint;
  private String ratePlanInfoEndpoint;
  private String preferencesEndpoint;
  private String restrictionsByDateRangeEndpoint;
  private String multiHotelRestrictionsByDateRangeEndpoint;
  private String lightweightReservationsEndpoint;
  private String onSaleFlagEndpoint;
  private String availabilityByIdsEndpointV3;
  private String getVacantRoomsEndpoint;
  private String reservationPreferences;
  private String reservationInfo;
}
