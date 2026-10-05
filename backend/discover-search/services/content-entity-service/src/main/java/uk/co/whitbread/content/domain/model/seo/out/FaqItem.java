package uk.co.whitbread.content.domain.model.seo.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaqItem {

  private String question;
  private String answer;

}
