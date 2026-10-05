package uk.co.whitbread.shared.auth.tenant.jwt;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.log.LogMessage;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import uk.co.whitbread.shared.auth.tenant.Tenant;
import uk.co.whitbread.shared.auth.tenant.TenantRepository;

@RequiredArgsConstructor
public class TenantJwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

  private static final String DEFAULT_AUTHORITY_PREFIX = "";
  private static final String DEFAULT_AUTHORITY_CLAIM_SUFFIX = "/role";
  private static final String UNKNOWN_TENANT_NAMESPACE = "unknown tenant namespace";
  private static final String AUTHORITIES_CLAIM_NAME_EMPTY = "authoritiesClaimName cannot be empty";
  private static final String AUTHORITY_PREFIX_NULL = "authorityPrefix cannot be null";
  private static final String AUTHORITY_CLAIM_SUFFIX_NULL = "authorityClaimSuffix cannot be null";
  private static final String TRACE_LOOKING_FOR_SCOPES = "Looking for scopes in claim %s";
  private static final String AUTHORITIES_SPLIT_DELIMITER = " ";

  private final TenantRepository tenantRepository;
  private final Log logger = LogFactory.getLog(this.getClass());
  private String authorityPrefix = DEFAULT_AUTHORITY_PREFIX;
  private String authorityClaimSuffix = DEFAULT_AUTHORITY_CLAIM_SUFFIX;

  public Collection<GrantedAuthority> convert(Jwt jwt) {
    return this.getAuthorities(jwt)
        .stream()
        .map(authority -> new SimpleGrantedAuthority(this.authorityPrefix + authority))
        .collect(Collectors.toList());
  }

  public void setAuthorityPrefix(String authorityPrefix) {
    Assert.notNull(authorityPrefix, AUTHORITY_PREFIX_NULL);
    this.authorityPrefix = authorityPrefix;
  }

  public void setAuthorityClaimSuffix(String authorityClaimSuffix) {
    Assert.notNull(authorityClaimSuffix, AUTHORITY_CLAIM_SUFFIX_NULL);
    this.authorityClaimSuffix = authorityClaimSuffix;
  }

  private String getAuthoritiesClaimName(Jwt jwt) {
    String authoritiesClaimName = Optional.ofNullable(this.tenantRepository.findById(jwt.getIssuer().toString()))
        .map(Tenant::getNamespace)
        .orElseThrow(() -> new IllegalArgumentException(UNKNOWN_TENANT_NAMESPACE));
    Assert.hasText(authoritiesClaimName, AUTHORITIES_CLAIM_NAME_EMPTY);
    return authoritiesClaimName + authorityClaimSuffix;
  }

  private Collection<String> getAuthorities(Jwt jwt) {
    String claimName = this.getAuthoritiesClaimName(jwt);
    if (this.logger.isTraceEnabled()) {
      this.logger.trace(LogMessage.format(TRACE_LOOKING_FOR_SCOPES, claimName));
    }

    Object authorities = jwt.getClaim(claimName);
    if (authorities instanceof String auth) {
      return StringUtils.hasText(auth) ? Arrays.asList(((String) authorities).split(AUTHORITIES_SPLIT_DELIMITER)) : Collections.emptyList();
    } else {
      return (authorities instanceof Collection auth ? auth : Collections.emptyList());
    }
  }
}
