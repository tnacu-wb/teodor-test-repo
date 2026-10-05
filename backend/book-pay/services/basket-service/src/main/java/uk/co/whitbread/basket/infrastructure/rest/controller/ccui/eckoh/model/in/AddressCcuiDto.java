package uk.co.whitbread.basket.infrastructure.rest.controller.ccui.eckoh.model.in;

import static uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.validation.ValidBillingAddressValidator.LENGTH_INTERVAL;
import static uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.validation.ValidBillingAddressValidator.SPECIAL_CHARACTERS;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
public class AddressCcuiDto implements SelfValidation<AddressCcuiDto> {

  @Size(max = 100, message = LENGTH_INTERVAL)
  @Pattern(regexp = "^[ a-zA-Z0-9-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấßÜÖÄüöä/'.,-]*$",
      message = SPECIAL_CHARACTERS)
  private String addressLine1;
  @Size(max = 100, message = LENGTH_INTERVAL)
  @Pattern(regexp = "^[ a-zA-Z0-9-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấßÜÖÄüöä/'.,-]*$",
      message = SPECIAL_CHARACTERS)
  private String addressLine2;
  @Size(max = 100, message = LENGTH_INTERVAL)
  @Pattern(regexp = "^[ a-zA-Z0-9-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấßÜÖÄüöä/'.,-]*$",
      message = SPECIAL_CHARACTERS)
  private String addressLine3;
  @Size(max = 100, message = LENGTH_INTERVAL)
  @Pattern(regexp = "^[ a-zA-Z0-9-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấßÜÖÄüöä/'.,-]*$",
      message = SPECIAL_CHARACTERS)
  private String addressLine4;
  private String country;
  private String postalCode;

  public AddressCcuiDto(String addressLine1, String addressLine2, String addressLine3,
      String addressLine4, String country, String postalCode) {
    this.addressLine1 = addressLine1;
    this.addressLine2 = addressLine2;
    this.addressLine3 = addressLine3;
    this.addressLine4 = addressLine4;
    this.country = country;
    this.postalCode = postalCode;
    this.validateSelf();
  }
}
