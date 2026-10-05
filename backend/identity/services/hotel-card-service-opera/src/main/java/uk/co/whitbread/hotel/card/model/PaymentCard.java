package uk.co.whitbread.hotel.card.model;

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
public class PaymentCard {

  private String cardId;
  private String cardLabel;
  private String cardType;
  private String cardNumber;
  private String startDate;
  private String expiryDate;
  private String issueNumber;
  private String cardHolderName;
  private String cardToken;
  private Address billingAddress;
  private boolean cnpRequired;
  private String cnpBusinessAccountUsername;
  private String cnpBusinessAccountPassword;
}
