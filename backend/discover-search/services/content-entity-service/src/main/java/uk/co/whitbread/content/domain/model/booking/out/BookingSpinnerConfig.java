package uk.co.whitbread.content.domain.model.booking.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingSpinnerConfig {

  private String order;

  private String seconds;

  private String text;
}
