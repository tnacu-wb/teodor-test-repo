package uk.co.whitbread.content.infrastructure.rest.client.aem.model.dlp.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeoDto {

  private String pageTitle;
  private String pageDescription;
  private List<HreflangDto> hreflangs;
  private String geoJsonLd;
}
