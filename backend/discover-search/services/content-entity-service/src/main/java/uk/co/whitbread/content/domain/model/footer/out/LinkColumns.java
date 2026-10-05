package uk.co.whitbread.content.domain.model.footer.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkColumns {

  private String name;
  private List<LinkItems> linkItems;

}
