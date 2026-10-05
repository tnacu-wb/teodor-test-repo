package uk.co.whitbread.spending.domain.model.out.worldline;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentData {
  @JsonProperty("paymentDate")
  private String paymentDate;

  @JsonProperty("paymentDescription")
  private String paymentDescription;

  @JsonProperty("failureReason")
  private String failureReason;

  @JsonProperty("paymentValue")
  private PaymentValue paymentValue;
}
