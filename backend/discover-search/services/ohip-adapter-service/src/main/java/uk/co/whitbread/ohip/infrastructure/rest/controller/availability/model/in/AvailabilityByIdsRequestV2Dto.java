package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityByIdsRequestV2Dto {

  private BookingChannelDto bookingChannel;
  @NotNull
  private List<String> hotelIds;
  @NotNull
  private LocalDate arrivalDate;
  @NotNull
  private LocalDate departureDate;
  @NotNull
  private List<RoomV2Dto> rooms;
  @NotNull
  private RateV2Dto rates;
}
