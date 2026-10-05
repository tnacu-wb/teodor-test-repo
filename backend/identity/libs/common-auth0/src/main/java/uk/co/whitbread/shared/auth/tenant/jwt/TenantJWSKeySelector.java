package uk.co.whitbread.shared.auth.tenant.jwt;

import static uk.co.whitbread.shared.auth.tenant.jwt.Constants.UNKNOWN_TENANT;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.proc.JWSAlgorithmFamilyJWSKeySelector;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.JWTClaimsSetAwareJWSKeySelector;
import java.net.URL;
import java.security.Key;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.shared.auth.tenant.Tenant;
import uk.co.whitbread.shared.auth.tenant.TenantRepository;

@Slf4j
@RequiredArgsConstructor
public class TenantJWSKeySelector
    implements JWTClaimsSetAwareJWSKeySelector<SecurityContext> {

  private static final String ISSUER_CLAIM = "iss";
  private static final String JWK_FETCH_ERROR = "Cannot fetch the JWK";

  private final TenantRepository tenantRepository;
  private final Map<String, JWSKeySelector<SecurityContext>> selectors = new ConcurrentHashMap<>();

  @Override
  public List<? extends Key> selectKeys(JWSHeader jwsHeader, JWTClaimsSet jwtClaimsSet, SecurityContext securityContext)
      throws KeySourceException {
    return this.selectors.computeIfAbsent(toTenant(jwtClaimsSet), this::fromTenant)
        .selectJWSKeys(jwsHeader, securityContext);
  }

  private String toTenant(JWTClaimsSet claimSet) {
    return (String) claimSet.getClaim(ISSUER_CLAIM);
  }

  private JWSKeySelector<SecurityContext> fromTenant(String tenant) {
    return Optional.ofNullable(this.tenantRepository.findById(tenant))
        .map(Tenant::getJwkUri)
        .map(this::fromUri)
        .orElseThrow(() -> {
          log.error(UNKNOWN_TENANT);
          return new IllegalArgumentException(UNKNOWN_TENANT);
        });
  }

  private JWSKeySelector<SecurityContext> fromUri(String uri) {
    try {
      return JWSAlgorithmFamilyJWSKeySelector.fromJWKSetURL(new URL(uri));
    } catch (Exception ex) {
      log.error(JWK_FETCH_ERROR, ex);
      throw new IllegalArgumentException(ex);
    }
  }
}
