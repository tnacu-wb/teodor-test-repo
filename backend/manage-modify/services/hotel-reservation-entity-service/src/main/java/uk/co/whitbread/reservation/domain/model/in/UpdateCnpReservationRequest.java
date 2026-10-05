package uk.co.whitbread.reservation.domain.model.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCnpReservationRequest {

  private String language;
  private String countryCode;
  private BusinessAccountCnp businessAccount;
  private BookerDetailsCnp booker;
}
