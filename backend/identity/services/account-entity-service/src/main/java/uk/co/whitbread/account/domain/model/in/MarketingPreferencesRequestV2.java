package uk.co.whitbread.account.domain.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class MarketingPreferencesRequestV2 {

  private String contactType;
  private boolean optIn;
  private boolean doubleOptIn;
  private boolean secondPartyOptIn;
  private boolean thirdPartyVendorsOptIn;
  private String[] brandCodes;
  private Customer customer;
  private SourceDetails sourceDetails;
  private String contactValue;

}
