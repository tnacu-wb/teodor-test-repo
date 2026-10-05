package uk.co.whitbread.account.infrastructure.rest.client.marketing.model.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.account.infrastructure.rest.controller.account.model.in.CustomerDto;
import uk.co.whitbread.account.infrastructure.rest.controller.account.model.in.SourceDetailsDto;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarketingPreferencesRequestDto {
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
