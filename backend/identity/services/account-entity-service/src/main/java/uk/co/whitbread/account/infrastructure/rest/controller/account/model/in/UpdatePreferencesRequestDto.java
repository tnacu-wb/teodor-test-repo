package uk.co.whitbread.account.infrastructure.rest.controller.account.model.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class UpdatePreferencesRequestDto {
  private boolean optIn;
  private boolean doubleOptIn;
  @NotNull
  private String[] brandCodes;
  @NotNull
  @Valid
  private CustomerDto customer;
  @Valid
  private SourceDetailsDto sourceDetails;
}
