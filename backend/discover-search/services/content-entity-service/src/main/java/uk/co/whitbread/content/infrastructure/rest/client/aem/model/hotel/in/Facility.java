package uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Facility {

  private String code;
  private String legend;
  private String description;
  private Integer ranking;
  private String iconSrc;
  private Boolean visibility;
}