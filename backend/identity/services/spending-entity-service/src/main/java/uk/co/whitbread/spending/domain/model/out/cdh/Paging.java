package uk.co.whitbread.spending.domain.model.out.cdh;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Paging {

  private Integer totalResults;
  private Integer currentPage;
  private Integer pageSize;
}
