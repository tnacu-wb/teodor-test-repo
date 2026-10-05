package uk.co.whitbread.content.domain.model.cookies.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CookieGroup {

  private String cookieName;
  private String title;
  private String description;
  private Boolean isAlwaysActive;
  private String toggleLabel;

}
