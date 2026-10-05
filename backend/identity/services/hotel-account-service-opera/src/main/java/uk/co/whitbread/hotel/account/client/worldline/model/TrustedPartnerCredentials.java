package uk.co.whitbread.hotel.account.client.worldline.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrustedPartnerCredentials {
  @JsonProperty("Username")
  private String username;

  @JsonProperty("Password")
  private String password;
}
