package uk.co.whitbread.reservation.domain.model.out;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationPaymentCardType {

  private String cardNumberMasked;

  private String token;

  private LocalDate expirationDate;

  private String cardType;

  private String cardHolderName;

  private String paymentMethod;

  private Integer folioView;
}
