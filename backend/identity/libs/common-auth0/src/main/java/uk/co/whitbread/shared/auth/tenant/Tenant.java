package uk.co.whitbread.shared.auth.tenant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tenant {

  private String name;
  private String issuer;
  private String jwkUri;
  private String namespace;
  private String audience;
}
