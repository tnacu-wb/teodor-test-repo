package uk.co.whitbread.company.domain.model.out;

import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class CompaniesProfile {
  List<CompanyProfile> companies;
  int totalResults;
  boolean hasMore;
  int limit;
  int offset;
}
