package uk.co.whitbread.ohip.domain.model.reservation.in;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class StayingGuestAddress implements SelfValidation<StayingGuestAddress> {

  private String addressType;
  private String postalCode;
  private String addressLine1;
  private String addressLine2;
  private String addressLine3;
  private String addressLine4;
  private String countryCode;
  private String cityName;
  private String companyName;
  private String addressId;

  public StayingGuestAddress(String addressType, String postalCode, String addressLine1, String addressLine2,
      String addressLine3, String addressLine4, String countryCode, String cityName, String companyName,
                             String addressId) {
    this.addressType = addressType;
    this.postalCode = postalCode;
    this.addressLine1 = addressLine1;
    this.addressLine2 = addressLine2;
    this.addressLine3 = addressLine3;
    this.addressLine4 = addressLine4;
    this.countryCode = countryCode;
    this.cityName = cityName;
    this.companyName = companyName;
    this.addressId = addressId;
    this.validateSelf();
  }

}
