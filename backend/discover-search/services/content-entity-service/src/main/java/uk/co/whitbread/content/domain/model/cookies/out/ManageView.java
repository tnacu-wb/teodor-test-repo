package uk.co.whitbread.content.domain.model.cookies.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManageView {

  private String title;
  private String description;
  private String saveSettingsButtonText;
  private String alwaysActiveText;
  private List<CookieGroup> cookieGroup;

}
