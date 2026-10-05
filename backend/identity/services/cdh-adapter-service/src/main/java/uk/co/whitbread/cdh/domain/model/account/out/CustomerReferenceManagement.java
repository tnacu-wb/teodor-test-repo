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
public class CustomerReferenceManagement {
  @JsonProperty("Active")
  private boolean active;
  @JsonProperty("Label")
  private String label;
  @JsonProperty("Location")
  private String location;
  @JsonProperty("Header")
  private String header;
  @JsonProperty("Answers")
  private Answers answers;
  @JsonProperty("Mandatory")
  private boolean mandatory;
  @JsonProperty("Id")
  private String id;
  @JsonProperty("Type")
  private String type;
}
