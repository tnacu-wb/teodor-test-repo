package uk.co.whitbread.basket.domain.model.ccuieckoh.in;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardCcui {

  private String cardHolderFirstName;
  private String cardHolderLastName;
  private AddressCcui cardHolderAddress;

}
