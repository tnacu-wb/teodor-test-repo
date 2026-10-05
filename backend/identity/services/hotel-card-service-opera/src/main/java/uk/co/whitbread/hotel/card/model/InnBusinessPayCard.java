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
public class InnBusinessPayCard {

  private Boolean myCard;
  private String cardId;
  private String cardHolderName;
  private String cardRegistration;
  private String cardNumber;
  private CardStatusEnum cardStatus;
  private int cardRegistrationCount;
  private Boolean isActivated;


}
