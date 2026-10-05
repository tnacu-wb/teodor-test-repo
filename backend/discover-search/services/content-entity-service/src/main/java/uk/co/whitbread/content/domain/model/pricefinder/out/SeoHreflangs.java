package uk.co.whitbread.content.domain.model.pricefinder.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeoHreflangs {
  private String hreflang;
  private String href;
}
