package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCardDetailsDto {

  private String cardNumberMasked;

  private String token;

  private LocalDate expirationDate;

  private String cardType;

  private String cardHolderName;

  private String cardNumberLast4Digits;

  private String cardName;

  private String cardLogoSrc;

}
