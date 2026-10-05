package uk.co.whitbread.payapp.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardDetails {

  private String cardName;
  private Boolean myCard;
  private String cardOwnerName;
  private CreditLimit creditLimit;
  private String emailAddress;
  private String cardGuid;

}
