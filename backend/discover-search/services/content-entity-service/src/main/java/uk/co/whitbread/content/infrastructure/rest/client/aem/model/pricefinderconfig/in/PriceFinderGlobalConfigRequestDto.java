package uk.co.whitbread.content.infrastructure.rest.client.aem.model.pricefinderconfig.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceFinderGlobalConfigRequestDto {

  private String country;
  private String language;
  private String channelId;
  private String brand;
  private String path;

}
