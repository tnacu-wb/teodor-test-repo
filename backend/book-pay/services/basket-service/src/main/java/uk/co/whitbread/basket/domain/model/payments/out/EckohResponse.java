package uk.co.whitbread.basket.domain.model.payments.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EckohResponse {
  private String result;
  private int resultCode;
  private String paymentId;
  private String maskedPan;
  private String expiry;
  private String type;
  private String reference;
  private String token;
  private String content;
}
