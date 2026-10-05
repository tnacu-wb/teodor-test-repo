package uk.co.whitbread.content.infrastructure.rest.controller.booking.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingSpinnerConfigDto {

  private String order;

  private String seconds;

  private String text;
}
