package uk.co.whitbread.reservation.domain.model.index.header.data.out;

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
  private String pidLogo;
  private String zipLogo;
  private String pi;
  private String hub;
  private String pid;
  private String zip;
  private String hubLogo;
  private String hubBadge;
  private String zipBadge;
}
