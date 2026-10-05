package uk.co.whitbread.reservation;

import java.time.Instant;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import uk.co.whitbread.shared.auth.account.Account;
import uk.co.whitbread.shared.auth.security.model.CustomJwtAuthenticationToken;

public class WithMockCustomUserSecurityContextFactory
    implements WithSecurityContextFactory<WithJwtUser> {
    @Override
    public SecurityContext createSecurityContext(WithJwtUser jwtUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Account account = Account.builder()
            .email(jwtUser.email())
            .build();

        Jwt jwt = Jwt.withTokenValue("test")
            .expiresAt(Instant.MAX)
            .issuedAt(Instant.now())
            .header("test", "test")
            .issuer("test")
            .audience(List.of("test"))
            .build();

        Authentication auth =
            new CustomJwtAuthenticationToken(jwt, account);

        auth.setAuthenticated(true);

        context.setAuthentication(auth);
        return context;
    }
}