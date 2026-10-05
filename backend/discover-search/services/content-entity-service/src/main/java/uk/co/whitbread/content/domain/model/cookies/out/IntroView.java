package uk.co.whitbread.content.domain.model.cookies.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntroView {

  private String title;
  private String description;
  private String manageButtonText;
  private String acceptAllButtonText;
  private String necessaryOnlyButtonText;

}
