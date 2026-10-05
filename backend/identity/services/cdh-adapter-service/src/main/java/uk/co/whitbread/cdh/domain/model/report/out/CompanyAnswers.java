package uk.co.whitbread.cdh.domain.model.report.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyAnswers {

  @JsonProperty("QuestionId")
  private String questionId;

  @JsonProperty("Position")
  private Integer position;

  @JsonProperty("Label")
  private String label;

  @JsonProperty("Header")
  private String header;

  @JsonProperty("Answer")
  private String answer;

}
