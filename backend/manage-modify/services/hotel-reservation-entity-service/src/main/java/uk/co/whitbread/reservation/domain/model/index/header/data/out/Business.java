package uk.co.whitbread.reservation.domain.model.index.header.data.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Business {
  private String signupButton;
  private String tab;
  private String loginButton;
  private String tabMobile;
  private String formLabel;
  private String emailPlaceholder;
  private Banner banner;
  private String submitButton;
  private String formTitle;
  private String bookingDashboardRedirectPath;
  private String forgotPasswordLink;
  private String signupUrl;
}
