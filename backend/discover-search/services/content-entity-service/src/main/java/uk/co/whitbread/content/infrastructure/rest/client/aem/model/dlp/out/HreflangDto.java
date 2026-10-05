package uk.co.whitbread.content.infrastructure.rest.client.aem.model.dlp.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HreflangDto {

  private String hreflang;
  private String href;
}
