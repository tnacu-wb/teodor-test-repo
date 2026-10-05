package uk.co.whitbread.payments.infrastructure.rest.controller.payment.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentActionResponseDto {

  @JsonProperty("displayPaymentPage")
  private boolean displayPaymentPage;

  @JsonProperty("paymentActions")
  private List<PaymentActionDto> paymentActions;
}

