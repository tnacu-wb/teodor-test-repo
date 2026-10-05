package uk.co.whitbread.reservation.domain.model.payment.in;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Guest {

  private String name;
  private boolean registered;
  private LocalDate registeredSince;
  private int previousBookings;
}
