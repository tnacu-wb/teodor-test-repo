package uk.co.whitbread.ohip.domain.model.profile.out;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class CompanyProfileWrapper {
  private CompanyProfile companyProfile;
}
