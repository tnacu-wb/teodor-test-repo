package uk.co.whitbread.content.infrastructure.rest.controller.cookies.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CookieGroupDto {

  private String cookieName;
  private String title;
  private String description;
  private Boolean isAlwaysActive;
  private String toggleLabel;

}
