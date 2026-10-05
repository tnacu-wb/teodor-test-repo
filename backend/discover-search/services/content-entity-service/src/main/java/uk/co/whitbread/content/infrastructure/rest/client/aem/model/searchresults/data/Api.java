package uk.co.whitbread.content.infrastructure.rest.client.aem.model.searchresults.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Api {

  private String initialPageSize;
  private String radius;
  private String lazyLoadPageSize;
}
