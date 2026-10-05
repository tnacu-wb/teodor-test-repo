package uk.co.whitbread.ohip.domain.model.profile.out;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Company;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class CompanyWrapper {
  private Company company;
}
