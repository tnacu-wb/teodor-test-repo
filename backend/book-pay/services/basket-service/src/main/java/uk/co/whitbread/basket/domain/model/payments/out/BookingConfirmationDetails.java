package uk.co.whitbread.basket.domain.model.payments.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingConfirmationDetails {

  private String paymentType;
  private String paymentMethod;
  private String cardType;
  private String citId;
  private CardData cardData;
  private String ccAgentId;
}
