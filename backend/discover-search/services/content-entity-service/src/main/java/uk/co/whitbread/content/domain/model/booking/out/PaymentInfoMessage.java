package uk.co.whitbread.content.domain.model.booking.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInfoMessage {
  private String rate;
  private String paymentType;
  private List<String> messages;
}
