package uk.co.whitbread.basket.domain.model.payments.out;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Guest {

  private String name;
  private boolean registered;
  private LocalDate registeredSince;
  private int previousBookings;
}
