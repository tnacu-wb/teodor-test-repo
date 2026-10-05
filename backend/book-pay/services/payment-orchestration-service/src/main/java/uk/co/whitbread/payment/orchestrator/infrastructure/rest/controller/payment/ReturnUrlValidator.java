package uk.co.whitbread.payment.orchestrator.infrastructure.rest.controller.payment;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.payment.orchestrator.infrastructure.config.PaymentSecurityProperties;

/**
 * Validates returnUrl values for security against open-redirect attacks.
 *
 * <p>Enforces two rules:
 * <ol>
 *   <li>The URL must use the HTTPS protocol (HTTP is rejected).</li>
 *   <li>The URL host must exactly equal, or be a subdomain of, a host in the configured
 *       allowlist.</li>
 * </ol>
 *
 * <p>Allowlisted hosts are configured via the {@code payment.security.allowed-return-url-hosts}
 * application property. An allowlist entry such as {@code premierinn.digital} also permits its
 * subdomains (for example {@code feature-x.dev.premierinn.digital}), so ephemeral environments
 * are covered without per-host configuration.
 */
@Component
@Slf4j
public class ReturnUrlValidator {

  private final List<String> allowedHosts;

  public ReturnUrlValidator(PaymentSecurityProperties securityProperties) {
    this.allowedHosts = securityProperties.getAllowedReturnUrlHosts().stream()
        .map(host -> host.toLowerCase(Locale.ROOT))
        .toList();
  }

  /**
   * Validates that the given returnUrl uses HTTPS and its host is in the allowlist.
   *
   * @param returnUrl the URL to validate
   * @return {@code true} if the URL passes both HTTPS and host allowlist checks
   */
  public boolean isValidReturnUrl(String returnUrl) {
    if (returnUrl == null || returnUrl.isBlank()) {
      return false;
    }

    try {
      URI uri = new URI(returnUrl);

      if (!"https".equalsIgnoreCase(uri.getScheme())) {
        log.debug("returnUrl rejected: non-HTTPS scheme '{}'", uri.getScheme());
        return false;
      }

      String host = uri.getHost();
      if (host == null || !isHostAllowed(host.toLowerCase(Locale.ROOT))) {
        log.debug("returnUrl rejected: host '{}' not in allowlist", host);
        return false;
      }

      return true;
    } catch (URISyntaxException e) {
      log.debug("returnUrl rejected: malformed URI '{}'", returnUrl);
      return false;
    }
  }

  /**
   * Matches a host against the allowlist. A host is allowed when it exactly equals an allowlisted
   * entry, or is a subdomain of one (ends with {@code "." + entry}). The leading-dot boundary
   * prevents suffix-bypass hosts such as {@code premierinn.digital.evil.com} or
   * {@code notpremierinn.digital}.
   *
   * @param host the lower-cased host to check
   * @return {@code true} if the host equals or is a subdomain of an allowlisted host
   */
  private boolean isHostAllowed(String host) {
    return allowedHosts.stream()
        .anyMatch(allowed -> host.equals(allowed) || host.endsWith("." + allowed));
  }
}
