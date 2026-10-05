package uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.in.ccui;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardCcuiDto {

  @NotEmpty
  private String cardHolderFirstName;

  @NotEmpty
  private String cardHolderLastName;

  @NotNull
  @Valid
  private AddressCcuiDto cardHolderAddress;
}
