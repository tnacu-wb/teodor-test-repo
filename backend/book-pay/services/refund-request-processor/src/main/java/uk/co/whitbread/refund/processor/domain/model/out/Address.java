package uk.co.whitbread.refund.processor.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Address {

  private String line1;
  private String line2;
  private String line3;
  private String line4;
  private String countryCode;
  private String postalCode;
}