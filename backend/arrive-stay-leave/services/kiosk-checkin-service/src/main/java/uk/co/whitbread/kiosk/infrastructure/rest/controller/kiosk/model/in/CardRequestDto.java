package uk.co.whitbread.kiosk.infrastructure.rest.controller.kiosk.model.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardRequestDto {

  @NotEmpty
  private String cardholderName;
  @NotEmpty
  private String cardType;
  @NotEmpty
  private String expiryMonth;
  @NotEmpty
  private String expiryYear;
  @NotEmpty
  private String token;
  private boolean cnpRequired;
  private String logoUrl;
  private String type;
  @NotEmpty
  private String cardSchemeId;
  private String cardSchemeName;
}
