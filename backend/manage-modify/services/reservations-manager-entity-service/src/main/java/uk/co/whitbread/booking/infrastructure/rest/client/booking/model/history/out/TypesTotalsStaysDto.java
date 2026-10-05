package uk.co.whitbread.booking.infrastructure.rest.client.booking.model.history.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TypesTotalsStaysDto {

  private int upcoming;
  private int cancelled;
  private int past;
  private int checkedIn;
}
