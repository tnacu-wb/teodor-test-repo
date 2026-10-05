package uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCardDto {
  private String cardId;
  private Integer position;
  private BillingAddressDto billingAddress;
  private CardNotPresentDto cardNotPresent;
  private String cardLabel;
  private boolean cardNotPresentRequired;
  private String cardNumber;
  private String token;
  private String cardType;
  private String expiryDate;
  private String nameOnCard;
  private String created;
  private String modified;
  private boolean deleted;
  private LocalDate dateDeleted;
}
