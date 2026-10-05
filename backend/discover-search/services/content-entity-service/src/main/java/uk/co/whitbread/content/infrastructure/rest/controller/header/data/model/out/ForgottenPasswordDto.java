package uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForgottenPasswordDto {

  private LeisureDto leisure;
  private BusinessDto business;
  private String cancel;
  private String notRegisteredError;
  private String emailSentHeader;
  private String genericError;
  private String emailSentMessage;
  private String backToLogin;
  private String backToYourDetails;
}
