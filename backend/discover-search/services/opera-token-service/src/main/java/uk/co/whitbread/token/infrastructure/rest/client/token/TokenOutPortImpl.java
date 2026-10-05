package uk.co.whitbread.token.infrastructure.rest.client.token;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.token.domain.model.out.AuthToken;
import uk.co.whitbread.token.domain.ports.secondary.TokenOutPort;
import uk.co.whitbread.token.infrastructure.rest.client.token.mapper.OperaTokenRequestMapper;
import uk.co.whitbread.token.infrastructure.rest.client.token.ohip.OhipTokenClient;


@Slf4j
@RequiredArgsConstructor
public class TokenOutPortImpl implements TokenOutPort {

  private final OhipTokenClient ohipTokenClient;
  private final  OperaTokenRequestMapper operaTokenRequestMapper;

  @Override
  public AuthToken getToken(String providerId) {
    log.debug("Entered getToken");
    var tokenResponse = ohipTokenClient.getAccessToken(providerId);
    return operaTokenRequestMapper.toResultDto(tokenResponse);
  }

}
