package uk.co.whitbread.basket.domain.model.payments.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessAmendRequest {

  private String language;
  private String channel;
  private String paymentOptionSelected;
}
