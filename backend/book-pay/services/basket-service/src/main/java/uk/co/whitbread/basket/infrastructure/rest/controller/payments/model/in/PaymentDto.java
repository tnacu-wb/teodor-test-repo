package uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDto {

  @NotEmpty
  private String type;
  @NotEmpty
  private String subType;
  private String environment;
  private CardDto card;
  private AmountDto amount;
  private BillingDto billing;
  private ScaDto sca;
  private BusinessItemsDto businessItems;
  private Boolean pibaCardPresent;
  private String paypalNonce;
  private String paypalDeviceData;
}
