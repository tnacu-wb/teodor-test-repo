package uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.in;

import lombok.Data;

@Data
public class VerfiyOtpProvisionRequestDto {
  private String otpCode;
  private String otpType;
  private String otpDestination;
  private String shortPropertyCode;
  private String firstName;
  private String lastName;
  private String bookingReference;
}
