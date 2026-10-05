package uk.co.whitbread.shared.auth.tenant.jwt;

import static uk.co.whitbread.shared.auth.tenant.jwt.Constants.UNKNOWN_TENANT;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import uk.co.whitbread.shared.auth.tenant.Tenant;
import uk.co.whitbread.shared.auth.tenant.TenantRepository;

@RequiredArgsConstructor
@Slf4j
public class TenantJwtIssuerValidator implements OAuth2TokenValidator<Jwt> {

  private final TenantRepository tenantRepository;
  private final Map<String, JwtIssuerValidator> validators = new ConcurrentHashMap<>();

  @Override
  public OAuth2TokenValidatorResult validate(Jwt token) {
    return this.validators.computeIfAbsent(toTenant(token), this::fromTenant)
        .validate(token);
  }

  private String toTenant(Jwt jwt) {
    return jwt.getIssuer().toString();
  }

  private JwtIssuerValidator fromTenant(String tenant) {
    return Optional.ofNullable(this.tenantRepository.findById(tenant))
        .map(Tenant::getIssuer)
        .map(JwtIssuerValidator::new)
        .orElseThrow(() -> {
          log.error(UNKNOWN_TENANT);
          return new IllegalArgumentException(UNKNOWN_TENANT);
        });
  }
}