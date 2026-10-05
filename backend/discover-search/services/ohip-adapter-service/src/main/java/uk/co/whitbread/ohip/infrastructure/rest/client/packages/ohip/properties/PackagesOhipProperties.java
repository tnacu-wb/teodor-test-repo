package uk.co.whitbread.ohip.infrastructure.rest.client.packages.ohip.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class PackagesOhipProperties {

  private final String packagesEndpoint;
  private final String packageGroupsEndpoint;

  public PackagesOhipProperties(@Value("${config.service.ohip.packagesEndpoint}") String packagesEndpoint,
      @Value("${config.service.ohip.packageGroupsEndpoint}") String packageGroupsEndpont) {
    this.packagesEndpoint = packagesEndpoint;
    this.packageGroupsEndpoint = packageGroupsEndpont;
  }
}
