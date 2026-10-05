package uk.co.whitbread.avail.business.events.infrastructure.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class OauthTokenResponse implements Serializable {

  @JsonProperty("expires_in")
  private String expiresAt;

  @JsonProperty("token_type")
  private String tokenType;

  @JsonProperty("oracle_tk_context")
  private String oracleTokenContext;

  @JsonProperty("refresh_token")
  private String refreshToken;

  @JsonProperty("oracle_grant_type")
  private String oracleGrantType;

  @JsonProperty("access_token")
  private String accessToken;

}

