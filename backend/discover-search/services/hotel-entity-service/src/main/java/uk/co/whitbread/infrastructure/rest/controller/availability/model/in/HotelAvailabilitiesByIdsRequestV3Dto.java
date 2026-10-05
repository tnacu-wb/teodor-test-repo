package uk.co.whitbread.infrastructure.rest.controller.availability.model.in;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.infrastructure.rest.controller.validation.ArrivalDepartureDateConstraintForLocalDateV3;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ArrivalDepartureDateConstraintForLocalDateV3
public class HotelAvailabilitiesByIdsRequestV3Dto {

  @NotNull
  private BookingChannelDto bookingChannel;
  @NotNull
  private List<String> hotelIds;

  @NotNull
  @FutureOrPresent
  private LocalDate arrivalDate;

  @NotNull
  @Future
  private LocalDate departureDate;

  @NotNull
  private List<RoomDto> rooms;
  @NotNull
  private RateV3Dto rates;
  private boolean vatNotRequired;
  private Boolean isOTA;

}
