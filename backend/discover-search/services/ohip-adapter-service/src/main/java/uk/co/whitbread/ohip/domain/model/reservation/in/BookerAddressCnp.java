package uk.co.whitbread.ohip.domain.model.reservation.in;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookerAddressCnp {

  private String postalCode;
  private String addressLine1;
  private String addressLine2;
  private String addressLine3;
  private String addressLine4;
}
