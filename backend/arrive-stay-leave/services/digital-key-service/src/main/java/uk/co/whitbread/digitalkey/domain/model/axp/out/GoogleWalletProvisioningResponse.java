package uk.co.whitbread.digitalkey.domain.model.axp.out;

import lombok.Data;

@Data
public class GoogleWalletProvisioningResponse {
  private String provisioningUrl;
  private String credentialToken;
}