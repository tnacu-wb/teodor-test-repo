package uk.co.whitbread.hotel.register.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePreferencesRequest {

  private String contactType;
  private boolean optIn;
  private boolean doubleOptIn;
  private boolean secondPartyOptIn;
  private boolean thirdPartyVendorsOptIn;
  private String[] brandCodes;
  private MarketingCustomer customer;
  private ContactSubType contactSubType;
  private SourceDetails sourceDetails;
  private String contactValue;
}
