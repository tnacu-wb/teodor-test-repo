package uk.co.whitbread.infrastructure.rest.client.cache.model;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import uk.co.whitbread.domain.model.srp.in.HotelAvailabilitiesRequest;

@AllArgsConstructor
public class OperaHotelAvailabilitiesKey implements Serializable {

  private final String keyType;
  private final String location;
  private final Integer radius;
  private final String radiusUnit;
  private final String arrivalDate;
  private final String departureDate;
  private final List<Integer> adultsNumber;
  private final List<Integer> childrenNumber;
  private final List<String> roomTypes;
  private final String channel;
  private final String subChannel;
  private final String companyId;
  private final String sort;
  private final Float rcPriceModifier;
  private final Float rcDistanceModifier;

  public static OperaHotelAvailabilitiesKey fromAvailabilitiesRequest(
      String keyType,
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest) {
    return new OperaHotelAvailabilitiesKey(
        keyType,
        hotelAvailabilitiesRequest.getLocation(),
        hotelAvailabilitiesRequest.getRadius(),
        hotelAvailabilitiesRequest.getRadiusUnit().name(),
        hotelAvailabilitiesRequest.getArrivalDate(),
        hotelAvailabilitiesRequest.getDepartureDate(),
        hotelAvailabilitiesRequest.getAdultsNumber(),
        hotelAvailabilitiesRequest.getChildrenNumber(),
        hotelAvailabilitiesRequest.getRoomTypes(),
        hotelAvailabilitiesRequest.getChannel(),
        hotelAvailabilitiesRequest.getSubChannel(),
        hotelAvailabilitiesRequest.getCompanyId(),
        hotelAvailabilitiesRequest.getSort(),
        hotelAvailabilitiesRequest.getRcPriceModifier(),
        hotelAvailabilitiesRequest.getRcDistanceModifier()
    );
  }

  @Override
  public String toString() {
    StringBuilder keyBuilder = new StringBuilder();
    keyBuilder.append("OperaHotelAvailabilitiesKey::")
        .append(keyType).append(",")
        .append(location).append(",")
        .append(radius).append(",")
        .append(radiusUnit).append(",")
        .append(arrivalDate).append(",")
        .append(departureDate).append(",")
        .append(adultsNumber).append(",")
        .append(childrenNumber).append(",")
        .append(roomTypes).append(",")
        .append(channel).append(",")
        .append(subChannel).append(",")
        .append(companyId);

    if ("RECOMMENDATION".equals(sort)) {
      keyBuilder.append(",").append(rcDistanceModifier)
          .append(",").append(rcPriceModifier);
    }

    return keyBuilder.toString();
  }
}