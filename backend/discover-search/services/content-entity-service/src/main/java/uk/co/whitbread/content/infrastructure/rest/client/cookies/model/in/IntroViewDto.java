package uk.co.whitbread.content.infrastructure.rest.client.cookies.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntroViewDto {

  private String title;
  private String description;
  private String manageButtonText;
  private String acceptAllButtonText;
  private String necessaryOnlyButtonText;

}
