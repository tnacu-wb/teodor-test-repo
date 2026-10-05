package uk.co.whitbread.basket.domain.model.payments.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CdhSearchCompaniesResponse {

  private String continuationToken;
  private List<Company> results;
  private Integer searchResults;
  private Integer totalResults;

}