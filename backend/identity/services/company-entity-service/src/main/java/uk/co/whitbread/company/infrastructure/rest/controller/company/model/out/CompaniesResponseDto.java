package uk.co.whitbread.company.infrastructure.rest.controller.company.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CompaniesResponseDto {

  private List<CompanyResponseDto> companies;
  private int totalResults;
  private boolean hasMore;
  private int limit;
  private int offset;
  private int page;
  private int pageSize;
}