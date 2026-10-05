package uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDetailsDto {

  private String commentTitle;
  private String type;
  private String textValue;

}
