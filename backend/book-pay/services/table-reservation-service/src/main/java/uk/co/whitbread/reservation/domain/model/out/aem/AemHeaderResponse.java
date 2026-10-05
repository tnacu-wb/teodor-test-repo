package uk.co.whitbread.reservation.domain.model.out.aem;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AemHeaderResponse {

  private String logoSrc;
  private String logoAlt;
  private String homeAltSrc;
  private String moreLocationName;
  private List<String> locationSrc;
  private List<NavbarItem> navbar;
}
