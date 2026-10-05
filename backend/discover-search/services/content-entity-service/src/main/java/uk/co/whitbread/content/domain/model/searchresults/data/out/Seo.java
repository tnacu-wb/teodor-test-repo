package uk.co.whitbread.content.domain.model.searchresults.data.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Seo {

  private String pageDescription;
  private String cardImageUrl;
  private String pageTitle;
  private List<Hreflang> hreflangs;
}
