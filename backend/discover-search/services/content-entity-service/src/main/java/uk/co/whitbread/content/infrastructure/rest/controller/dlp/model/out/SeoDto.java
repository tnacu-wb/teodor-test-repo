package uk.co.whitbread.content.infrastructure.rest.controller.dlp.model.out;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SeoDto {

  private String pageTitle;
  private String pageDescription;
  private List<HreflangDto> hreflangs;
  private String geoJsonLd;
}
