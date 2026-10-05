package uk.co.whitbread.account.infrastructure.rest.client.marketing.model.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.account.infrastructure.rest.controller.account.model.in.CustomerDto;
import uk.co.whitbread.account.infrastructure.rest.controller.account.model.in.SourceDetailsDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarketingPreferencesRequestV2Dto {
  private String contactType;
  private boolean optIn;
  private boolean doubleOptIn;
  private boolean secondPartyOptIn;
  private boolean thirdPartyVendorsOptIn;
  private String[] brandCodes;
  private CustomerDto customer;
  private SourceDetailsDto sourceDetails;
  private String contactValue;
}
