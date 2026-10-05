package uk.co.whitbread.employee.bulk.model.companyEmployees;

import lombok.Data;

@Data
public class PaymentPreferencePaymentCard {

  private BillingAddress billingAddress;
  private String cardHolderName;
  private String cardNumber;
  private String cardType;
  private String cnpBusinessAccountPassword;
  private String cnpBusinessAccountUsername;
  private Boolean cnpRequired;
  private String expiryDate;
  private String token;
}
