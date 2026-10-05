package uk.co.whitbread.basket.infrastructure.rest.controller.ccui.payment.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CcuiPaymentRequestDto {

  private CcuiExtraItemsDto ccuiExtraItems;
  private PaymentCcuiRequestDto paymentRequest;
  private String paymentOption;
  private String subPaymentType;
  private Boolean useCache;
}
