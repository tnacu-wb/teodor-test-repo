package uk.co.whitbread.basket.domain.model.ccuieckoh.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CcuiPaymentRequest {

  private CcuiExtraItems ccuiExtraItems;
  private PaymentCcuiRequest paymentRequest;
  private Boolean sendMail;
  private String paymentOption;
  private String subPaymentType;
  private Boolean useCache;
}
