package uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentPreferencePaymentCardDto {
  private BillingAddressDto billingAddress;
  private String cardHolderName;
  private String cardNumber;
  private String cardType;
  private String expiryDate;
  private boolean cnpRequired;
  private String token;
  private String cnpBusinessAccountPassword;
  private String cnpBusinessAccountUsername;
}
