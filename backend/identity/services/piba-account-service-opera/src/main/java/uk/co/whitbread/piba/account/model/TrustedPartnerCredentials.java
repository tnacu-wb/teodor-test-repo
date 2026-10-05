package uk.co.whitbread.piba.account.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrustedPartnerCredentials {
  @JsonProperty("Username")
  private String username;

  @JsonProperty("Password")
  private String password;
}
