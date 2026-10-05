package uk.co.whitbread.basket.domain.model.payments.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.basket.domain.model.business.in.BusinessItems;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
public class Payment implements SelfValidation<Payment> {

  @NotEmpty
  private String type;
  @NotEmpty
  private String subType;
  private String environment;
  private Card card;
  private Amount amount;
  private Billing billing;
  private Sca sca;
  private BusinessItems businessItems;
  private Boolean pibaCardPresent;
  private String paypalNonce;
  private String paypalDeviceData;

  public Payment(String type, String subType, String environment, Card card, Amount amount,
      Billing billing, Sca sca, BusinessItems businessItems, Boolean pibaCardPresent, String paypalNonce,
                 String paypalDeviceData) {
    this.type = type;
    this.subType = subType;
    this.environment = environment;
    this.card = card;
    this.amount = amount;
    this.billing = billing;
    this.sca = sca;
    this.businessItems = businessItems;
    this.pibaCardPresent = pibaCardPresent;
    this.paypalNonce = paypalNonce;
    this.paypalDeviceData = paypalDeviceData;
    this.validateSelf();
  }
}
