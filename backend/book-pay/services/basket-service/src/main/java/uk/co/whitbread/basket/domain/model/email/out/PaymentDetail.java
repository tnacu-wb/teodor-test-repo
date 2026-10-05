package uk.co.whitbread.basket.domain.model.email.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetail {

  private String paymentId;
  private String cardNo;
  private String cardType;
  private BigDecimal amount;
  private String currency;
  private String transactionTimestamp;
}