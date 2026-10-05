package uk.co.whitbread.cdh.domain.model.account.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Questions {
  @JsonProperty("Id")
  private String id;
  @JsonProperty("Position")
  private String position;
  @JsonProperty("Label")
  private String label;
  @JsonProperty("Mandatory")
  private boolean mandatory;
  @JsonProperty("Header")
  private String header;
  @JsonProperty("Location")
  private String location;
  @JsonProperty("Answers")
  private Answers answers;
}
