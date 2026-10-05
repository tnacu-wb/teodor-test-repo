package uk.co.whitbread.reservation.domain.model.payment.in.ccui;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardCcui {

  @NotEmpty
  private String cardHolderFirstName;

  @NotEmpty
  private String cardHolderLastName;

  @NotNull
  private AddressCcui cardHolderAddress;
}
