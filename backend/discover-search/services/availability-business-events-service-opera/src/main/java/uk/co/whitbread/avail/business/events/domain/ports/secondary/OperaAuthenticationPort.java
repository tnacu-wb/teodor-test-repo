package uk.co.whitbread.avail.business.events.domain.ports.secondary;

public interface OperaAuthenticationPort {

  String fetchOauthToken(final boolean isInvalidToken);
}
