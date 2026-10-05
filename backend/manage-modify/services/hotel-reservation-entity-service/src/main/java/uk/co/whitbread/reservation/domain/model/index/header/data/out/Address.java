package uk.co.whitbread.reservation.domain.model.index.header.data.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

  private String addressLine1;

  private String addressLine2;

  private String addressLine3;

  private String country;

  private String postalCode;

}
