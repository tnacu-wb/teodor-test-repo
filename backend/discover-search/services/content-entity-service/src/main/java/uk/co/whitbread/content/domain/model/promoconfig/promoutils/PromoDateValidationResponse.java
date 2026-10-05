package uk.co.whitbread.content.domain.model.promoconfig.promoutils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PromoDateValidationResponse {
  private boolean bookingValid;
  private boolean bookingInvalid;
  private boolean bookingExpired;
  private boolean stayValid;
  private boolean nightsValid;
}