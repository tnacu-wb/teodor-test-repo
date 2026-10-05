package uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChevronDto {

  private String up;
  private String right;
  private String left;
  private String down;
  private String rightPurple;
  private String leftPurple;
  private String upPurple;
  private String downPurple;

}
