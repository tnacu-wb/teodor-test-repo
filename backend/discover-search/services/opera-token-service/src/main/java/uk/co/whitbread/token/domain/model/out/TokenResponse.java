package uk.co.whitbread.token.domain.model.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenResponse {

  private String accessToken;

  private String tokenType;

  private int expiresIn;

  private String issuedAt;
}
