package uk.co.whitbread.content.infrastructure.rest.controller.dlp.model.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FaqItemDto {

  private String question;
  private String answer;
}
