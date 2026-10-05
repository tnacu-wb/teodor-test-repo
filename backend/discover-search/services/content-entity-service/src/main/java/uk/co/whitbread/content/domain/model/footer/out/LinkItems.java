package uk.co.whitbread.content.domain.model.footer.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkItems {

  private String name;
  private String linkSrc;
  private Boolean openInNewTab;
}
