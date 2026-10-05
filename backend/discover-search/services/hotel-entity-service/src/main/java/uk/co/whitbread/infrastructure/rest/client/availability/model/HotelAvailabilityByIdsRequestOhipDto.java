package uk.co.whitbread.infrastructure.rest.client.availability.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelAvailabilityByIdsRequestOhipDto {

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
  private String globalCompanyId;
  private List<String> negotiatedRateDisplaySets;
  private List<String> pmsRoomTypes;
}
