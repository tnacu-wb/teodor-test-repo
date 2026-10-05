package uk.co.whitbread.basket.infrastructure.rest.controller.ccui.payment.model.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardCcuiDto {

  @NotEmpty
  private String cardHolderFirstName;

  @NotEmpty
  private String cardHolderLastName;

  @NotNull
  @Valid
  private AddressCcuiDto cardHolderAddress;

}
