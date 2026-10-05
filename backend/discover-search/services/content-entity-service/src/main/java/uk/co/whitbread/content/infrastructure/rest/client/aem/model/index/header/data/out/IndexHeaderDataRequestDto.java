package uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndexHeaderDataRequestDto {

  private String country;
  private String language;
  private Boolean businessBooker;

}
