package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.shared.commons.validation.CompanyName;

@Data
@Builder
public class StayingGuestAddressDto {

  private String addressType;
  private String postalCode;
  private String addressLine1;
  private String addressLine2;
  private String addressLine3;
  private String addressLine4;
  private String countryCode;
  private String cityName;
  @CompanyName
  private String companyName;
  private String addressId;

  /**
   * This unnecessary method is a hotfix for DNRQ-63419.
   * This should be removed once Address schema is tightened with field validation
   * and corresponding changes are added to FE channels
   *
   * @return true if a bare minimum address is present
   */
  public boolean isValid() {
    return
        this.postalCode != null
        && this.addressLine1 != null
        && this.countryCode != null;
  }
}
