package uk.co.whitbread.shared.cdh.model.question;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Question {

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
