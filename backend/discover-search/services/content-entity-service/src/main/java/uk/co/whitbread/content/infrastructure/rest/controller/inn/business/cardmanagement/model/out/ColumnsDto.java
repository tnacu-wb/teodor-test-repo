package uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColumnsDto {

  private String cardId;
  private String cardLabel;
  private String expiry;
  private String yourCard;
  private String cardHolderName;
  private String cardHolderRegistered;
  private String cardNumber;
  private String cardStatus;
  private String edit;

}
