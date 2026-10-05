package uk.co.whitbread.ohip.infrastructure.rest.client.lov.ohip.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class ListOfValuesOhipProperties {

  private final String cancellationReasonsEndpoint;

  public ListOfValuesOhipProperties(
      @Value("${config.service.ohip.cancellationReasonsEndpoint}") String cancellationReasonsEndpoint) {
    this.cancellationReasonsEndpoint = cancellationReasonsEndpoint;
  }

}
