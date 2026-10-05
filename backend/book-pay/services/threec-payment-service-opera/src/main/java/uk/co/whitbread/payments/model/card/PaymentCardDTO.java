package uk.co.whitbread.payments.model.card;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentCardDTO {

  private Boolean business;
  private Boolean personalCard;

  private String customerAccountId;
  private String companyAccountId;
  private String employeeAccountId;
  private String userEmail;

  private String cardToken;
  private String cardNumberLast4Digits;
  private String expiryDate;
  private String cardType;
  private String cardHolderName;

  private AddressDTO billingAddress;

  private Boolean cnpRequired;
  private String cnpBusinessAccountUsername;
  private String cnpBusinessAccountPassword;
  private String cardId;
  private String cardLabel;
}