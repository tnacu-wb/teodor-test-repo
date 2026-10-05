package uk.co.whitbread.shared.azureemail.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OtpPasswordReset {

  private final String language;
  private final String emailAddress;
  private final String customerName;
  private final String otp;
}
