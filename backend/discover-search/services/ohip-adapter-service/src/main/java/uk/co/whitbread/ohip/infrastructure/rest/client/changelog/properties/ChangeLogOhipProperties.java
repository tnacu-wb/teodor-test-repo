package uk.co.whitbread.ohip.infrastructure.rest.client.changelog.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class ChangeLogOhipProperties {

  String activityLogEndpoint;

  public ChangeLogOhipProperties(@Value("${config.service.ohip.activityLogEndpoint}")String activityLogEndpoint) {
    this.activityLogEndpoint = activityLogEndpoint;
  }
}
