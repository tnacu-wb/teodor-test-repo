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
public class LinkTabsDto {

  private String name;
  private IntroDto intro;
  private List<LinkColumnsDto> columns;
}
