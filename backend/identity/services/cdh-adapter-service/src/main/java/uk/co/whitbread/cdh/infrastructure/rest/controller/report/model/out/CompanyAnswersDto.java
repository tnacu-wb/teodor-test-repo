package uk.co.whitbread.cdh.infrastructure.rest.controller.report.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyAnswersDto {

  private String questionId;
  private Integer position;
  private String label;
  private String header;
  private String answer;

}
