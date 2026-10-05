package uk.co.whitbread.account.domain.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class MarketingPreferencesRequest {
  private boolean optIn;
  private boolean doubleOptIn;
  private String[] brandCodes;
  private Customer customer;
  private SourceDetails sourceDetails;
}
