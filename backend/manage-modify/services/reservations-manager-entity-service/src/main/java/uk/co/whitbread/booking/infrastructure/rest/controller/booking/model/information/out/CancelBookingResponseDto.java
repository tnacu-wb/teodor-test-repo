package uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.information.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CancelBookingResponseDto {

  private String bookingReference;
  private String cancellationId;
  private String sourceSystem;
}