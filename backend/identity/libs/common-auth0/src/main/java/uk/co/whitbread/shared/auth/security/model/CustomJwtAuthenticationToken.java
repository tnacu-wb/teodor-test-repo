package uk.co.whitbread.shared.auth.security.model;

import java.io.Serial;
import java.util.Collection;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.Transient;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import uk.co.whitbread.shared.auth.account.Account;

@EqualsAndHashCode(callSuper = true)
@Transient
public class CustomJwtAuthenticationToken extends JwtAuthenticationToken {

  @Serial
  private static final long serialVersionUID = -3075691188007945727L;
  private final Account account;

  public CustomJwtAuthenticationToken(Jwt jwt, Account account) {
    super(jwt);
    this.account = account;
  }

  public CustomJwtAuthenticationToken(Jwt jwt,
      Collection<? extends GrantedAuthority> authorities,
      Account account) {
    super(jwt, authorities);
    this.account = account;
  }

  public CustomJwtAuthenticationToken(Jwt jwt,
      Collection<? extends GrantedAuthority> authorities,
      String name,
      Account account) {
    super(jwt, authorities, name);
    this.account = account;
  }

  public Account getAccount() {
    return account;
  }
}
