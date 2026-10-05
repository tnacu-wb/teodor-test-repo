package uk.co.whitbread.content.domain.model.searchresults.data.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Brand {

  private String zipLogoWhite;
  private String piLogo;
  private String hubLogoWhite;
  private String pidLogo;
  private String zipBadge;
  private String pi;
  private String hub;
  private String zip;
  private String hubBadge;
  private String hubLogo;
  private String hubHdpLogo;
}
