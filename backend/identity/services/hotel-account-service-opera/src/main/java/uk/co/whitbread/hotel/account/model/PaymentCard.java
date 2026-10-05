package uk.co.whitbread.hotel.account.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.hotel.account.validation.PaymentDetails;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@PaymentDetails
public class PaymentCard implements Serializable {

  private static final long serialVersionUID = 1L;

  private String cardID;
  private String cardLabel;
  private String cardType;
  private String cardNumber;
  private String startDate;
  private String expiryDate;
  private String issueNumber;
  private String cardHolderName;
  private String cardToken;
  private BillingAddress billingAddress;
  private Boolean cnpRequired;
  private String cnpBusinessAccountUsername;
  private String cnpBusinessAccountPassword;
}