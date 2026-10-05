package uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chevron {

  private String up;
  private String right;
  private String left;
  private String down;
  private String rightPurple;
  private String leftPurple;
  private String upPurple;
  private String downPurple;

}
