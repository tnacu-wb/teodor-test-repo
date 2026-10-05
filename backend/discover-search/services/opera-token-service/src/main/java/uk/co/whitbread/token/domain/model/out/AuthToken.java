package uk.co.whitbread.token.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthToken {

  private String accessToken;

  private String tokenType;

  private long expiresIn;

  private String issuedAt;

}
