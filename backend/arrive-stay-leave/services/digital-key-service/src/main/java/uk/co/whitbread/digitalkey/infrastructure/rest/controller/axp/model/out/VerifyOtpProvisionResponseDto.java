package uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.out;

import lombok.Data;

@Data
public class VerifyOtpProvisionResponseDto {
  private String provisioningCredentialIdentifier;
  private String sharingInstanceIdentifier;
  private String cardConfigurationIdentifier;
  private String cardTemplateIdentifier;
  private String serverEnvironmentIdentifier;
  private String accountHash;
  private String relyingPartyIdentifier;
}
