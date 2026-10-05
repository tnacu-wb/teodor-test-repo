package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CookieGroupDto {

  private String cookieName;
  private String title;
  private String description;
  private boolean alwaysActive;
  private String toggleLabel;

}