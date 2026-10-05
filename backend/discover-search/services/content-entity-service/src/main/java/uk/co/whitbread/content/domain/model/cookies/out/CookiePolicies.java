package uk.co.whitbread.content.domain.model.cookies.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CookiePolicies {

  private String version;
  private CookieDurationConfig config;
  private String brand;
  private IntroView introView;
  private ManageView manageView;

}
