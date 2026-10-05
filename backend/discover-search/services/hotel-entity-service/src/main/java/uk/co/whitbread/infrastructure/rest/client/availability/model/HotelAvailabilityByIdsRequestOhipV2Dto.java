package uk.co.whitbread.infrastructure.rest.client.availability.model;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.domain.model.availability.in.Rate;

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
  private Rate rates;
}
