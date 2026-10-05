package uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardDetailsDto {

  private String cardName;
  private Boolean myCard;
  private String cardOwnerName;
  private CreditLimitDto creditLimit;
  private String emailAddress;
  @JsonProperty("cardGUID")
  private String cardGuid;

}
