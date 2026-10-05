package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.model;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelAvailabilityByIdsRequestOhipV2Dto {

  private BookingChannelDto bookingChannel;
  private List<String> hotelIds;
  private LocalDate arrivalDate;
  private LocalDate departureDate;
  private List<RoomV2Dto> rooms;
  private RateDto rates;
}
