package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.validation.DateFormat;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.validation.DateRange;

@Data
@DateRange
public class AmendStayDatesRequestDto {

  @NotNull
  private String tempBookingRef;

  @NotNull
  @DateFormat
  private String newStartDate;

  @NotNull
  @DateFormat
  private String newEndDate;

  @NotNull
  private BookingChannelDto bookingChannel;

  private String token;
}
