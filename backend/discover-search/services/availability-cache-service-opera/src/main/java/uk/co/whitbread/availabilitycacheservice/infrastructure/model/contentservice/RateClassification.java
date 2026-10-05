package uk.co.whitbread.availabilitycacheservice.infrastructure.model.contentservice;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RateClassification implements Serializable {

  @JsonProperty("rateClassification")
  private String classification;
  @JsonProperty("rateOrder")
  private String order;
  @JsonProperty("rateName")
  private String name;
  @JsonProperty("rateDescription")
  private String description;
  @JsonProperty("rateNotes")
  private String notes;
  @JsonProperty("rateLongDescription")
  private String longDescription;

}
