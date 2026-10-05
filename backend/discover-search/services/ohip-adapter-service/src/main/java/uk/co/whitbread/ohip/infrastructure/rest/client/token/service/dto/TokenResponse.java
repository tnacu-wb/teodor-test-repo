package uk.co.whitbread.ohip.infrastructure.rest.client.token.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import lombok.Data;

@Data
public class TokenResponse {

  @JsonProperty("accessToken")
  private String accessToken;

  @JsonProperty("tokenType")
  private String tokenType;

  @JsonProperty("expiresIn")
  private Integer expiresIn; // seconds

  @JsonProperty("issuedAt")
  private String issuedAt; // ISO timestamp

  /**
   * Calculate the expiration time based on issuedAt + expiresIn.
   *
   * @throws IllegalStateException if issuedAt or expiresIn is missing
   */
  public Instant getExpirationTime() {
    if (issuedAt == null) {
      throw new IllegalStateException(
          "Token response missing required field 'issuedAt'. Cannot determine token expiration time.");
    }
    if (expiresIn == null) {
      throw new IllegalStateException(
          "Token response missing required field 'expiresIn'. Cannot determine token expiration time.");
    }
    return Instant.parse(issuedAt).plusSeconds(expiresIn);
  }
}


