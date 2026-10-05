package uk.co.whitbread.account.infrastructure.rest.controller.account.model.in;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdatePreferencesRequestV2Dto {
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
