package uk.co.whitbread.content.domain.model.index.header.data.out;

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
