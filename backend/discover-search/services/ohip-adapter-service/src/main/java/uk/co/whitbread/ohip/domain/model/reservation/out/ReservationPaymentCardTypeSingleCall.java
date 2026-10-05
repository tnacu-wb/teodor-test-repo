package uk.co.whitbread.ohip.domain.model.reservation.out;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationPaymentCardTypeSingleCall {
  private String cardNumberMasked;

  private String token;

  private LocalDate expirationDate;

  private String cardType;

  private String cardHolderName;

  private String paymentMethod;
}