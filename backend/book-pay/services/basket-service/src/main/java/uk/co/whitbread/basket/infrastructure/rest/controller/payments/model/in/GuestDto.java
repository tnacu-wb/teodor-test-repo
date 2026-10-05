package uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
public class GuestDto implements SelfValidation<GuestDto> {

  private String name;
  private boolean registered;
  private LocalDate registeredSince;
  private int previousBookings;

  public GuestDto(String name, boolean registered, LocalDate registeredSince,
      int previousBookings) {
    this.name = name;
    this.registered = registered;
    this.registeredSince = registeredSince;
    this.previousBookings = previousBookings;
    this.validateSelf();
  }
}
