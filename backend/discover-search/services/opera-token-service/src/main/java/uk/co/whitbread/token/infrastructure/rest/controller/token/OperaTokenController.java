package uk.co.whitbread.token.infrastructure.rest.controller.token;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.token.domain.ports.primary.TokenInPort;
import uk.co.whitbread.token.infrastructure.rest.controller.token.mapper.TokenRequestDtoMapper;
import uk.co.whitbread.token.infrastructure.rest.controller.token.model.out.AuthTokenDto;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/tokens")
public class OperaTokenController implements OperaTokenApiDocumentation {

  private final TokenInPort tokenInPort;
  private final TokenRequestDtoMapper tokenRequestDtoMapper;

  @GetMapping("{providerId}/access-token")
  public ResponseEntity<AuthTokenDto> getToken(@PathVariable String providerId) {
    var token = tokenInPort.getToken(providerId);
    AuthTokenDto response = tokenRequestDtoMapper.toDto(token);

    return ResponseEntity.ok(response);
  }

}
