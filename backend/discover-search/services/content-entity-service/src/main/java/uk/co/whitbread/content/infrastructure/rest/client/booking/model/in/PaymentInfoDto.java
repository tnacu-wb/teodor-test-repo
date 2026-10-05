package uk.co.whitbread.content.infrastructure.rest.client.booking.model.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInfoDto {

  private String rate;
  private String rateCategory;
  private String rateDisplaySet;
  private String paymentType;
  private List<MessageDto> messages;

}
