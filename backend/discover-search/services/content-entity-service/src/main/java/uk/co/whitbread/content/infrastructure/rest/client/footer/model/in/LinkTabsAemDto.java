package uk.co.whitbread.content.infrastructure.rest.client.footer.model.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkTabsAemDto {

  private String tabTitle;
  private String introTitle;
  private String introDescription;
  private List<LinkColumnsAemDto> linkColumns;
}
