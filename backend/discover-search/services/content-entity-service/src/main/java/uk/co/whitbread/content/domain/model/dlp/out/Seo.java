package uk.co.whitbread.content.domain.model.dlp.out;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Seo {

  private String pageTitle;
  private String pageDescription;
  private List<Hreflang> hreflangs;
  private String geoJsonLd;
}
