package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationPaymentCardTypeDto {

  private String cardNumberMasked;
  private String token;
  private LocalDate expirationDate;
  private String cardType;
  private String cardHolderName;
  private String paymentMethod;
  private Integer folioView;
}
