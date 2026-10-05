package uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data;

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
  private String hubHdpLogo;
}
