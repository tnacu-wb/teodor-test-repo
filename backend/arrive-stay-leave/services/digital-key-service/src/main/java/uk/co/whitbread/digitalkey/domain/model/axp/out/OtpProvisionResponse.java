package uk.co.whitbread.digitalkey.domain.model.axp.out;

import lombok.Data;

@Data
public class OtpProvisionResponse {
  private String provisioningCredentialIdentifier;
  private String sharingInstanceIdentifier;
  private String cardConfigurationIdentifier;
  private String cardTemplateIdentifier;
  private String serverEnvironmentIdentifier;
  private String accountHash;
  private String relyingPartyIdentifier;
}
