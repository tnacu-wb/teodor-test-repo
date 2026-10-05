package uk.co.whitbread.content.infrastructure.rest.client.footer.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkItemsAemDto {

  private String linkText;
  private String linkPath;
  private String linkOpenNewTab;
}
