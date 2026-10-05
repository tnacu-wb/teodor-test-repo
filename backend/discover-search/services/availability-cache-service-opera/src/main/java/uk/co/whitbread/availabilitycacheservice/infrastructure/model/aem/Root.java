package uk.co.whitbread.availabilitycacheservice.infrastructure.model.aem;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.Data;

@Data
public class Root {

  @JsonProperty(value = ":items")
  private Map<String, Location> items;
}