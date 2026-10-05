package uk.co.whitbread.digitalkey.domain.model.axp.in;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GoogleWalletProvisioningRequest {
  @NotBlank private String otpCode;
  @NotBlank private String otpType;
  @NotBlank private String otpDestination;
  @NotBlank private String shortPropertyCode;
  @NotBlank private String firstName;
  @NotBlank private String lastName;
  @NotBlank private String bookingReference;
  @NotBlank private String linkingToken;
  @NotBlank private String walletUserId;
}