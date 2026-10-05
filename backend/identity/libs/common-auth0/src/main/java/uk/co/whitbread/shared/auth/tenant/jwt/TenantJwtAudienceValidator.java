package uk.co.whitbread.shared.auth.tenant.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import uk.co.whitbread.shared.auth.tenant.TenantRepository;

@Slf4j
@RequiredArgsConstructor
public class TenantJwtAudienceValidator implements OAuth2TokenValidator<Jwt> {

  private final TenantRepository tenantRepository;

  public OAuth2TokenValidatorResult validate(Jwt jwt) {
    var audiences = jwt.getAudience();
    var tokenIssuer = jwt.getIssuer().toString();
    var tenant = tenantRepository.findById(tokenIssuer);
    if (audiences != null &&
        tenant != null &&
        audiences.contains(tenant.getAudience())) {
      return OAuth2TokenValidatorResult.success();
    }
    log.error("Invalid token audience: {}", tokenIssuer);
    var err = new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN);
    return OAuth2TokenValidatorResult.failure(err);
  }
}
