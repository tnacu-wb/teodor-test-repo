package uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.out;

import lombok.Data;

@Data
public class GoogleWalletProvisioningResponseDto {
  private String provisioningUrl;
  private String credentialToken;
}