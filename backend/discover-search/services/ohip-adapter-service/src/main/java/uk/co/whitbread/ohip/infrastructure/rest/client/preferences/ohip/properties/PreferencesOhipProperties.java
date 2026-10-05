package uk.co.whitbread.ohip.infrastructure.rest.client.preferences.ohip.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class PreferencesOhipProperties {

  private final String preferencesEndpoint;

  public PreferencesOhipProperties(
      @Value("${config.service.ohip.preferencesEndpoint}") String preferencesEndpoint) {
    this.preferencesEndpoint = preferencesEndpoint;
  }
}
