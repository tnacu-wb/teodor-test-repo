package uk.co.whitbread.ohip.infrastructure.rest.controller.profile.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompaniesProfileDto {
  private List<CompanyProfileDto> companies;
  private int totalResults;
  private boolean hasMore;
  private int limit;
  private int offset;
}
