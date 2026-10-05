package uk.co.whitbread.token.domain.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import uk.co.whitbread.token.domain.model.out.AuthToken;
import uk.co.whitbread.token.domain.ports.secondary.TokenOutPort;

@ExtendWith(MockitoExtension.class)
class TokenInPortImplTest {

  private static final String PROVIDER_ID = "ohip";
  private static final String INVALID_PROVIDER = "invalid-provider";

  @Mock
  private ClientRegistrationRepository clientRegistrationRepository;
  @Mock
  private TokenOutPort tokenOutPort;

  @InjectMocks
  private TokenInPortImpl tokenInPortImpl;

  @Test
  void getToken_validProvider_shouldReturnToken() {
    // Arrange
    ClientRegistration registration = ClientRegistration
        .withRegistrationId(PROVIDER_ID)
        .clientId("test-client")
        .clientSecret("test-secret")
        .authorizationGrantType(org.springframework.security.oauth2.core.AuthorizationGrantType.CLIENT_CREDENTIALS)
        .tokenUri("https://test.example.com/oauth/token")
        .build();

    AuthToken expectedToken = new AuthToken(
        "test-token",
        "Bearer",
        3600L,
        "2025-01-01T00:00:00Z"
    );

    when(clientRegistrationRepository.findByRegistrationId(PROVIDER_ID)).thenReturn(registration);
    when(tokenOutPort.getToken(PROVIDER_ID)).thenReturn(expectedToken);

    // Act
    AuthToken result = tokenInPortImpl.getToken(PROVIDER_ID);

    // Assert
    assertNotNull(result);
    assertEquals(expectedToken, result);
    verify(clientRegistrationRepository).findByRegistrationId(PROVIDER_ID);
    verify(tokenOutPort).getToken(PROVIDER_ID);
  }

  @Test
  void getToken_invalidProvider_shouldThrowException() {
    // Arrange
    when(clientRegistrationRepository.findByRegistrationId(INVALID_PROVIDER)).thenReturn(null);

    // Act & Assert
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> tokenInPortImpl.getToken(INVALID_PROVIDER)
    );

    assertEquals("No such provider: " + INVALID_PROVIDER, exception.getMessage());
    verify(clientRegistrationRepository).findByRegistrationId(INVALID_PROVIDER);
  }

  @Test
  void getToken_nullProvider_shouldThrowException() {
    // Arrange
    when(clientRegistrationRepository.findByRegistrationId(null)).thenReturn(null);

    // Act & Assert
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> tokenInPortImpl.getToken(null)
    );

    assertEquals("No such provider: null", exception.getMessage());
  }
}
