package uk.co.whitbread.digitalkey.domain.model.axp.in;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterMobileDeviceRequest {
  @NotBlank private String shortPropertyCode;
  @NotBlank private String bookingReference;
  @NotBlank private String firstName;
  @NotBlank private String lastName;
  @NotBlank private String deviceId;
  @NotBlank private String deviceManufacturer;
  @NotBlank private String deviceModel;
  @NotBlank private String osVersion;
  @NotBlank private String appVersion;
}