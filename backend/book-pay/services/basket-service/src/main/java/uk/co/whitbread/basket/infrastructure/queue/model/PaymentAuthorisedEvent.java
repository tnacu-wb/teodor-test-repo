package uk.co.whitbread.basket.infrastructure.queue.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentsConfirmation;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentAuthorisedEvent {

  private String basketReference;
  private String paymentProvider;
  private PaymentsConfirmation paymentsConfirmation;

}
