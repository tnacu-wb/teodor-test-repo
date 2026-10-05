package uk.co.whitbread.reservation.infrastructure.rest.client.hotel.model;

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
public class HotelAvailabilityV2RequestDto {
  @NotNull
  private BookingChannelDto bookingChannel;
  @NotNull
  private List<String> hotelIds;
  @NotNull
  private LocalDate arrivalDate;
  @NotNull
  private LocalDate departureDate;
  @NotNull
  private List<RoomDto> rooms;
  @NotNull
  private RateDto rates;
  private boolean vatNotRequired;
  private Boolean isOTA;
}
