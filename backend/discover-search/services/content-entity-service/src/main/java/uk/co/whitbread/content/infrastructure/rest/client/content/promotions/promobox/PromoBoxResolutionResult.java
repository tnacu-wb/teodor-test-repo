package uk.co.whitbread.content.infrastructure.rest.client.content.promotions.promobox;

import lombok.AllArgsConstructor;
import lombok.Data;
import uk.co.whitbread.content.domain.model.promoconfig.promoutils.PromoBoxStatus;

@Data
@AllArgsConstructor
public class PromoBoxResolutionResult {

  private PromoBoxStatus status;
  private boolean terminal;

  public static PromoBoxResolutionResult terminal(
      PromoBoxStatus status) {
    return new PromoBoxResolutionResult(status, true);
  }

  public static PromoBoxResolutionResult nonTerminal() {
    return new PromoBoxResolutionResult(null, false);
  }
}