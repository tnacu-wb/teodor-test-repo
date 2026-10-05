package uk.co.whitbread.reservation.infrastructure.rest.client.hotel.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelAvailabilityRequestDto {
  private List<String> hotelIds;
  private String arrivalDate;
  private String departureDate;
  private List<String> roomTypes;
  private List<Integer> adultsNumber;
  private List<Integer> childrenNumber;
  private List<Boolean> cotsRequired;
  private List<String> ratePlanCodes;
  private String channel;
  private String subchannel;
  private String language;
  private String country;
  private String globalCompanyId;
  private String[] negotiatedRateDisplaySets;
  private boolean vatNotRequired;
  private List<String> pmsRoomTypes;
  private Boolean isOTA;
}
