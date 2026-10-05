package uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForgottenPassword {

  private String notRegisteredError;
  private String cancel;
  private String emailSentHeader;
  private String genericError;
  private String emailSentMessage;
  private String backToLogin;
  private String backToYourDetails;
  private Leisure leisure;
  private Business business;
}
