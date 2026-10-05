package uk.co.whitbread.shared.auth.security;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uk.co.whitbread.shared.auth.account.Account;
import uk.co.whitbread.shared.auth.security.model.CustomJwtAuthenticationToken;

@Service
@RequiredArgsConstructor
public class AuthenticatedUserService {

  public CustomJwtAuthenticationToken getAuthenticatedUser() {
    return (CustomJwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
  }

  public List<String> getAuthenticatedUserAuthorities() {
    return getAuthenticatedUser().getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .toList();
  }

  public boolean isUserAuthenticated() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication != null && authentication.isAuthenticated()
        && !(authentication instanceof AnonymousAuthenticationToken);
  }

  public Optional<Account> getCurrentUserAccount() {
    return isUserAuthenticated()
        ? Optional.of(this.getAuthenticatedUser().getAccount())
        : Optional.empty();
  }
}
