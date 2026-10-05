package uk.co.whitbread.availabilitycacheservice.infrastructure.model.aem;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AemLocationResponse {

  @JsonProperty(value = ":items")
  private AemItem items;
}
