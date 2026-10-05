package uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.in.ccui;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.reservation.domain.model.validator.SelfValidation;
import uk.co.whitbread.shared.commons.validation.CompanyName;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressCcuiDto implements SelfValidation<AddressCcuiDto> {

  private static final String SPECIAL_CHARACTERS = "Must not contain special characters";
  private static final String LENGTH_INTERVAL = "Size must be between 1 and 100";

  @Size(max = 35, message = LENGTH_INTERVAL)
  @Pattern(regexp = "^[ a-zA-Z0-9-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấßÜÖÄüöä/'.,-]*$",
      message = SPECIAL_CHARACTERS)
  private String addressLine1;
  @Size(max = 35, message = LENGTH_INTERVAL)
  @Pattern(regexp = "^[ a-zA-Z0-9-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấßÜÖÄüöä/'.,-]*$",
      message = SPECIAL_CHARACTERS)
  private String addressLine2;
  @Size(max = 35, message = LENGTH_INTERVAL)
  @Pattern(regexp = "^[ a-zA-Z0-9-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấßÜÖÄüöä/'.,-]*$",
      message = SPECIAL_CHARACTERS)
  private String addressLine3;
  @Size(max = 35, message = LENGTH_INTERVAL)
  @Pattern(regexp = "^[ a-zA-Z0-9-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấßÜÖÄüöä/'.,-]*$",
      message = SPECIAL_CHARACTERS)
  private String addressLine4;
  private String cityName;
  @CompanyName
  private String companyName;
  private String addressType;
  private String country;
  private String postalCode;

}
