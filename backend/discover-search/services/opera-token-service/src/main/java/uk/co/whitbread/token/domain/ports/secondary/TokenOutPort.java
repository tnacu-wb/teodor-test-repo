package uk.co.whitbread.token.domain.ports.secondary;


import uk.co.whitbread.token.domain.model.out.AuthToken;

public interface TokenOutPort {

  AuthToken getToken(String providerId);
}
