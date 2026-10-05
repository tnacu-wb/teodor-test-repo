package uk.co.whitbread.content.infrastructure.rest.client.cookies.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CookiePoliciesDto {

  private String version;
  private CookieDurationConfigDto config;
  private String brand;
  private IntroViewDto introView;
  private ManageViewDto manageView;

}
