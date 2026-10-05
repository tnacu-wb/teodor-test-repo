package uk.co.whitbread.content.infrastructure.rest.controller.footer.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkItemsDto {

  private String name;
  private String linkSrc;
  private Boolean openInNewTab;
}
