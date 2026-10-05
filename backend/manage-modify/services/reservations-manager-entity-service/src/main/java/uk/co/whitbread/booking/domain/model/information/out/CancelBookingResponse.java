package uk.co.whitbread.booking.domain.model.information.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CancelBookingResponse {

  private String bookingReference;
  private String cancellationId;
}
