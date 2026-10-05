package uk.co.whitbread.ohip.domain.model.checkin.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDetails {

  private String commentTitle;
  private String type;
  private String textValue;

}
