package uk.co.whitbread.ohip.domain.model.reservation.out;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@AllArgsConstructor
public class ReservationPaymentCardType {

  private UniqueIdType cardId;
  private CurrencyAmountType currentAuthorizedAmount;
  private CurrencyAmountType approvalAmountNeeded;
  private String approvalCode;
  private String cardType;
  private String userDefinedCardType;
  private String token;
  private String cardNumberMasked;
  private String cardNumberLast4Digits;
  private LocalDate expirationDate;
  private String expirationDateMasked;
  private Boolean expirationDateExpired;
  private String cardHolderName;
  private Boolean attachCreditCardToProfile;
  private String processing;
  private Boolean swiped;
  private Boolean cardPresent;
  private String cardOrToken;
  private String citId;
  private String paymentMethod;
  private Integer folioView;
}
