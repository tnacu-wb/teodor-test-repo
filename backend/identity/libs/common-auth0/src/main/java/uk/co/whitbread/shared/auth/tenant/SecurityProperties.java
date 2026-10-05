package uk.co.whitbread.shared.auth.tenant;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "auth")
public class SecurityProperties {

  private List<Tenant> tenants = new ArrayList<>();
  private String rulesEngineHost;
  private String isAllowedAccessForRoleIdsEndpoint;
}
