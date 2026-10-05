package uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CancelReservationResponseDto {

  private String basketReference;
}
