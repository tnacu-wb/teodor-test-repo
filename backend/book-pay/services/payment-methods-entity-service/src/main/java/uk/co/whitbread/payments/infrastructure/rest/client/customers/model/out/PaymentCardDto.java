package uk.co.whitbread.payments.infrastructure.rest.client.customers.model.out;


import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class PaymentCardDto {
  private BillingAddressDto billingAddress;
  private String cardHolderName;
  @JsonAlias({"cardID", "cardId"})
  private String cardId;
  private String cardNumber;
  private String cardToken;
  private String issueNumber;
  private String cardType;
  private String cnpBusinessAccountPassword;
  private String cnpBusinessAccountUsername;
  private Boolean cnpRequired;
  private String expiryDate;
  private String startDate;
}
