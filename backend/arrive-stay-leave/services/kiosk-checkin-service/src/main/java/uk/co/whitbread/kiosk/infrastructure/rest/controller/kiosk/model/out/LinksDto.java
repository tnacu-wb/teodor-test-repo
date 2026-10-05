package uk.co.whitbread.kiosk.infrastructure.rest.controller.kiosk.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinksDto {

  private String href;
  private String rel;
  private boolean templated;
  private String method;
  private String operationId;

}
