package uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.in;

import lombok.Data;

@Data
public class RegisterMobileDeviceRequestDto {
  private String shortPropertyCode;
  private String bookingReference;
  private String firstName;
  private String lastName;
  private String deviceId;
  private String deviceManufacturer;
  private String deviceModel;
  private String osVersion;
  private String appVersion;
}