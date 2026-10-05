package uk.co.whitbread.availabilitycacheservice.infrastructure.util;


import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.distribution.DistributionPayload;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.distribution.DistributionSearchCriteria;


@Slf4j
public final class DistributionHotelAvailabilitiesUtil {

  private DistributionHotelAvailabilitiesUtil() {

  }


  public static final DistributionPayload buildDistributionPayload(
      final DistributionSearchCriteria distributionSearchCriteria,
      final String[][] roomTypes,
      final Set<String> rateCodes) {
    return DistributionPayload.builder()
        .hotelCodes(distributionSearchCriteria.getHotelCodes())
        .arrival(distributionSearchCriteria.getArrival())
        .departure(distributionSearchCriteria.getDeparture())
        .cot(distributionSearchCriteria.getCot())
        .language(distributionSearchCriteria.getLanguage())
        .country(distributionSearchCriteria.getCountry())
        .adults(distributionSearchCriteria.getAdults())
        .children(distributionSearchCriteria.getChildren())
        .rooms(distributionSearchCriteria.getRooms())
        .roomQty(distributionSearchCriteria.getRoomQty())
        .roomTypes(roomTypes)
        .rateCodes(rateCodes)
        .build();
  }

  public static final DistributionPayload buildDistributionPayloadForDataValidation(
      final String arrival, final String departure) {
    return DistributionPayload.builder()
        .arrival(arrival)
        .departure(departure)
        .build();
  }

}
