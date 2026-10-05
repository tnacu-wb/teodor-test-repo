package uk.co.whitbread.content.domain.model.dlp.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FaqItem {

  private String question;
  private String answer;
}
