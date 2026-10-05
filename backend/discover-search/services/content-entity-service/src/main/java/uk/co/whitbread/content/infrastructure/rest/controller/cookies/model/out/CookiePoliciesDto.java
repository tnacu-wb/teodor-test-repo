package uk.co.whitbread.content.infrastructure.rest.controller.cookies.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.content.infrastructure.rest.client.cookies.model.in.CookieDurationConfigDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CookiePoliciesDto {

  private String version;
  private String brand;
  private CookieDurationConfigDto config;
  private IntroViewDto introView;
  private ManageViewDto manageView;


}
