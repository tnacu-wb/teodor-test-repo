package uk.co.whitbread.digitalkey.domain.model.axp.out;

import lombok.Data;

@Data
public class RegisterMobileDeviceResponse {
  private String mobileDeviceId;
  private Dormakaba dormakaba;

  @Data
  public static class Dormakaba {
    private String token;
    private String endpointId;
  }
}