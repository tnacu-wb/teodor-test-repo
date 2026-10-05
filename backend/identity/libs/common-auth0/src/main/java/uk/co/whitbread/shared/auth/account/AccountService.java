package uk.co.whitbread.shared.auth.account;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import uk.co.whitbread.shared.auth.tenant.TenantService;
import uk.co.whitbread.shared.auth.utils.JwtParser;

@Service
@RequiredArgsConstructor
@Deprecated(since = "Please use AuthenticatedUserService.class instead")
public class AccountService {

  private final TenantService tenantService;

  public Account getAccount(Jwt jwt) {

    var namespace = tenantService.getNamespace(jwt.getIssuer().toString());
    return JwtParser.toAccount(jwt, namespace);
  }
}
