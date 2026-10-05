package uk.co.whitbread.booking.infrastructure.rest.client.booking.model.information.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CancelBookingResponseDto {

  private String cancellationId;
}
