package uk.co.whitbread.reservation.domain.model.payment.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

  @NotEmpty
  private String addressLine1;
  private String addressLine2;
  private String addressLine3;
  private String addressLine4;
  private String country;
  private String postalCode;
}
