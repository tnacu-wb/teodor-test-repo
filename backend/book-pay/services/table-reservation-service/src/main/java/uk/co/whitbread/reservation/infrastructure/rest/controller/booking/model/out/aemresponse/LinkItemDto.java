package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LinkItemDto {

  private String name;
  private String linkSrc;
  private boolean openInNewTab;

}
