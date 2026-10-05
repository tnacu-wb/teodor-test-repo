package uk.co.whitbread.token.domain.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import uk.co.whitbread.token.domain.model.out.AuthToken;
import uk.co.whitbread.token.domain.ports.primary.TokenInPort;
import uk.co.whitbread.token.domain.ports.secondary.TokenOutPort;


@Slf4j
@RequiredArgsConstructor
public class TokenInPortImpl implements TokenInPort {

  private final ClientRegistrationRepository clientRegistrationRepository;
  private final TokenOutPort tokenOutPort;

  @Override
  public AuthToken getToken(String providerId) {
    if (!providerExists(providerId)) {
      throw new IllegalArgumentException("No such provider: " + providerId);
    }
    return tokenOutPort.getToken(providerId);
  }

  private boolean providerExists(String providerId) {
    return clientRegistrationRepository.findByRegistrationId(providerId) != null;
  }
}
