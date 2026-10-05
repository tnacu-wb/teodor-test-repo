package uk.co.whitbread.content.infrastructure.rest.controller.searchresults.data.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupItemsDto {

  private String label;
  private String info;
  private String codes;
  private String queryParam;
}
