package uk.co.whitbread.content.domain.model.inn.business.header.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.content.domain.model.index.header.data.out.InnBResults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Content {

  private Header header;
  private Global global;
  private List<Country> countries;
  private Form form;
  private InnBResults results;
  private Authentication authentication;
  private ContactBanner contactBanner;

}
