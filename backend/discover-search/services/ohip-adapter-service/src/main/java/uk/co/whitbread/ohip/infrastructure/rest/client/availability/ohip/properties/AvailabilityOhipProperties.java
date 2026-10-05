package uk.co.whitbread.ohip.infrastructure.rest.client.availability.ohip.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class AvailabilityOhipProperties {

  private final String availabilitiesEndpoint;
  private final String rateInfoEndpoint;
  private final String itemsInventoryEndpoint;
  private final String hotelInventoryEndpoint;
  private final String hotelInventoryStatisticsEndpoint;
  private final String itemInventoryHoldEndpoint;
  private final String roomTypesEndpoint;
  private final String multiHotelAvaEndpoint;
  private final String hubId;
  private final Integer maxAvailabilityConcurrency;
  private final Integer maxRequestedDays;
  private final Integer maxRequestedDaysRateInfo;
  private final String minimumRateAvaEndpoint;
  private final String multiRoomRateAvaEndpoint;
  private final String restrictionsByDateRangeEndpoint;

  public AvailabilityOhipProperties(
      @Value("${config.service.ohip.availabilityEndpoint}") String availabilitiesEndpoint,
      @Value("${config.service.ohip.rateInfoEndpoint}") String rateInfoEndpoint,
      @Value("${config.service.ohip.itemsInventoryEndpoint}") String itemsInventoryEndpoint,
      @Value("${config.service.ohip.hotelInventoryEndpoint}") String hotelInventoryEndpoint,
      @Value("${config.service.ohip.hotelInventoryStatisticsEndpoint}") String hotelInventoryStatisticsEndpoint,
      @Value("${config.service.ohip.itemInventoryHoldEndpoint}") String itemInventoryHoldEndpoint,
      @Value("${config.service.ohip.roomTypesEndpoint}") String roomTypesEndpoint,
      @Value("${config.service.ohip.multiHotelAvaEndpoint}") String multiHotelAvaEndpoint,
      @Value("${config.service.ohip.hubId}") String hubId,
      @Value("${config.service.ohip.maxAvailabilityConcurrency}") Integer maxAvailabilityConcurrency,
      @Value("${config.service.ohip.maxRequestedDays}") Integer maxRequestedDays,
      @Value("${config.service.ohip.maxRequestedDaysRateInfo}") Integer maxRequestedDaysRateInfo,
      @Value("${config.service.ohip.minimumRateAvaEndpoint}") String minimumRateAvaEndpoint,
      @Value("${config.service.ohip.multiRoomRateAvaEndpoint}") String multiRoomRateAvaEndpoint,
      @Value("${config.service.ohip.restrictionsByDateRangeEndpoint}") String restrictionsByDateRangeEndpoint) {
    this.availabilitiesEndpoint = availabilitiesEndpoint;
    this.rateInfoEndpoint = rateInfoEndpoint;
    this.itemsInventoryEndpoint = itemsInventoryEndpoint;
    this.itemInventoryHoldEndpoint = itemInventoryHoldEndpoint;
    this.hotelInventoryStatisticsEndpoint = hotelInventoryStatisticsEndpoint;
    this.hotelInventoryEndpoint = hotelInventoryEndpoint;
    this.roomTypesEndpoint = roomTypesEndpoint;
    this.multiHotelAvaEndpoint = multiHotelAvaEndpoint;
    this.hubId = hubId;
    this.maxAvailabilityConcurrency = maxAvailabilityConcurrency;
    this.maxRequestedDays = maxRequestedDays;
    this.maxRequestedDaysRateInfo = maxRequestedDaysRateInfo;
    this.minimumRateAvaEndpoint = minimumRateAvaEndpoint;
    this.multiRoomRateAvaEndpoint = multiRoomRateAvaEndpoint;
    this.restrictionsByDateRangeEndpoint = restrictionsByDateRangeEndpoint;
  }
}
