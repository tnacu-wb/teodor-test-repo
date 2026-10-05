package uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out;

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

  private List<HreflangDto> hreflangs;
  private String geoJsonLd;
}
