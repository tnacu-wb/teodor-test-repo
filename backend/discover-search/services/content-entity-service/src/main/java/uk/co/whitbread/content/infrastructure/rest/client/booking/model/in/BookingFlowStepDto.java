package uk.co.whitbread.content.infrastructure.rest.client.booking.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingFlowStepDto {

  private String id;
  private String step;
  private String title;

}
