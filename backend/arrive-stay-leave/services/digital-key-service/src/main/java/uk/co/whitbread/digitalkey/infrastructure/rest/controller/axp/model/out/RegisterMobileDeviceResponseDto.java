package uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.out;

import lombok.Data;

@Data
public class RegisterMobileDeviceResponseDto {
  private String mobileDeviceId;
  private DormakabaDto dormakaba;

  @Data
  public static class DormakabaDto {
    private String token;
    private String endpointId;
  }
}