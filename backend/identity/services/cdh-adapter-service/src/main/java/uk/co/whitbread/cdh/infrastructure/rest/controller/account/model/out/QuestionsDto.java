package uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionsDto {
  private String id;
  private String position;
  private String label;
  private boolean mandatory;
  private String header;
  private String location;
  private AnswersDto answers;
}
