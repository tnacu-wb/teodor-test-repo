package uk.co.whitbread.content.infrastructure.rest.controller.cookies.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManageViewDto {

  private String title;
  private String description;
  private String saveSettingsButtonText;
  private String alwaysActiveText;
  private List<CookieGroupDto> cookieGroup;

}
