package uk.co.whitbread.cdh.infrastructure.rest.client.oauth.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CdhOauthResponseDto {

  @JsonProperty("token_type")
  private String tokenType;

  @JsonProperty("expires_in")
  private long expiresIn;

  @JsonProperty("ext_expires_in")
  private long extExpiresIn;

  @JsonProperty("access_token")
  private String accessToken;
}
