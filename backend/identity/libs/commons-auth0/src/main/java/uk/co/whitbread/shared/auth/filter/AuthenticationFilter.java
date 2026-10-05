package uk.co.whitbread.shared.auth.filter;


import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.servlet.HandlerExceptionResolver;
import uk.co.whitbread.shared.auth.service.TokenService;
import uk.co.whitbread.shared.auth.exception.InvalidSessionException;
import uk.co.whitbread.shared.auth.properties.TokenProperties;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@RequiredArgsConstructor
public class AuthenticationFilter extends GenericFilterBean {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final TokenService tokenService;

    private final TokenProperties properties;
    private final HandlerExceptionResolver resolver;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        String path = req.getRequestURI();

        if (Optional.ofNullable(properties.getUrlPathRegex())
            .orElse(Collections.emptyList())
            .stream()
            .noneMatch(path::matches)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        try {
            String authorization = req.getHeader("Authorization");
            if (StringUtils.isBlank(authorization)) {
                throw new InvalidSessionException("Missing request header: Authorization");
            }
            Optional<String> extractedSessionId = tokenService.retrieveAndVerifyToken(authorization);

            if (!extractedSessionId.isPresent()) {
                throw new InvalidSessionException("Token not verified, 'session-id' not found in the Authorization header");
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        } catch (Exception e) {
            log.error("Spring Security Filter Chain Exception:", e);
            resolver.resolveException((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, null, e);
        }

    }
}
