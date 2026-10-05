package uk.co.whitbread.content.domain.model.seo.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hreflang {

  private String hreflang;
  private String href;
}
