package uk.co.whitbread.reservation.domain.model.payment.in.ccui;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.reservation.domain.model.validator.SelfValidation;


@Data
@Builder
@AllArgsConstructor
public class AddressCcui implements SelfValidation<AddressCcui> {
  private String addressLine1;
  private String addressLine2;
  private String addressLine3;
  private String addressLine4;
  private String countryCode;
  private String postalCode;
  private String cityName;
  private String companyName;
  private String addressType;

}
