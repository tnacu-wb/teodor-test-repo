package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelAvailabilityRequestOhipDto {

  private String hotelId;
  private String arrivalDate;
  private String departureDate;
  private List<String> roomTypes;
  private List<Integer> adultsNumber;
  private List<Integer> childrenNumber;
  private List<Boolean> cotsRequired;
  private String companyId;
  private String channel;
  private String subchannel;
  private String language;
}
