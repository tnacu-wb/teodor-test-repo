package uk.co.whitbread.basket.domain.model.payments.in;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
public class Guest implements SelfValidation<Guest> {

  private String name;
  private boolean registered;
  private LocalDate registeredSince;
  private int previousBookings;

  public Guest(String name, boolean registered, LocalDate registeredSince, int previousBookings) {
    this.name = name;
    this.registered = registered;
    this.registeredSince = registeredSince;
    this.previousBookings = previousBookings;
    this.validateSelf();
  }
}
