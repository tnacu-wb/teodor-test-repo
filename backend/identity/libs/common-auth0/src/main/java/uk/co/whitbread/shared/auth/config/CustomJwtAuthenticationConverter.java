package uk.co.whitbread.shared.auth.config;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.util.Assert;
import uk.co.whitbread.shared.auth.security.model.CustomJwtAuthenticationToken;
import uk.co.whitbread.shared.auth.tenant.TenantService;
import uk.co.whitbread.shared.auth.utils.JwtParser;

@RequiredArgsConstructor
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

  private final TenantService tenantService;
  private Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter =
      new JwtGrantedAuthoritiesConverter();
  private String principalClaimName = "sub";


  public final AbstractAuthenticationToken convert(Jwt jwt) {
    Collection<GrantedAuthority> authorities = this.jwtGrantedAuthoritiesConverter.convert(jwt);
    String principalClaimValue = jwt.getClaimAsString(this.principalClaimName);
    var account = JwtParser.toAccount(jwt, tenantService.getNamespace(jwt.getIssuer().toString()));
    return new CustomJwtAuthenticationToken(jwt, authorities, principalClaimValue, account);
  }

  public void setJwtGrantedAuthoritiesConverter(
      Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter) {
    Assert.notNull(jwtGrantedAuthoritiesConverter, "jwtGrantedAuthoritiesConverter cannot be null");
    this.jwtGrantedAuthoritiesConverter = jwtGrantedAuthoritiesConverter;
  }

  public void setPrincipalClaimName(String principalClaimName) {
    Assert.hasText(principalClaimName, "principalClaimName cannot be empty");
    this.principalClaimName = principalClaimName;
  }
}
