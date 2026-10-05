package uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Leisure {

  private String signupButton;
  private String tab;
  private String loginButton;
  private String tabMobile;
  private String formLabel;
  private String emailPlaceholder;
  private String submitButton;
  private String formTitle;
  private String forgotPasswordLink;
  private String signupUrl;
}
