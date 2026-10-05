package uk.co.whitbread.reservation.domain.model.out.aem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CookieGroup {
  private String cookieName;
  private String title;
  private String description;
  private boolean alwaysActive;
  private String toggleLabel;

}