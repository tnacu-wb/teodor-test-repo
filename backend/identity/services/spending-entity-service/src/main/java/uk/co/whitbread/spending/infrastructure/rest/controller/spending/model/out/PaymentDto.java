package uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentDto {

  private String paymentDate;

  private String paymentDescription;

  private String failureReason;

  private boolean paymentFailed;

  private PaymentValueDto paymentValue;
}
