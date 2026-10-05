package uk.co.whitbread.content.infrastructure.rest.controller.footer.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkColumnsDto {

  private String name;
  private List<LinkItemsDto> linkItems;

}
