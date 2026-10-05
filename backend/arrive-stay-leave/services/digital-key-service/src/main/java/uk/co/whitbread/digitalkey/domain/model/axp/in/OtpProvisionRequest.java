package uk.co.whitbread.digitalkey.domain.model.axp.in;

import lombok.Data;

@Data
public class OtpProvisionRequest {
  String bookingReference;
  String otpCode;
  String email;
  // will be used multiroom booking phase 2
  String reservationId;
}
