package uk.co.whitbread.token.domain.ports.primary;

import uk.co.whitbread.token.domain.model.out.AuthToken;

public interface TokenInPort {

  AuthToken getToken(String providerId);

}
