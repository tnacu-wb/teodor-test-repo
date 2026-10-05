package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReservationPaymentCardTypeDto {

  private String cardNumberMasked;
  private String token;
  private LocalDate expirationDate;
  private String cardType;
  private String cardHolderName;
  private String paymentMethod;
  private Integer folioView;
}
