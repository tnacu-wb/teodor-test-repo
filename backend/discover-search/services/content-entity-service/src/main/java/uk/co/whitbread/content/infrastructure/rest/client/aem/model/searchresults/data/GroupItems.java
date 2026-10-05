package uk.co.whitbread.content.infrastructure.rest.client.aem.model.searchresults.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupItems {

  private String label;
  private String info;
  private String codes;
  private String queryParam;
}
