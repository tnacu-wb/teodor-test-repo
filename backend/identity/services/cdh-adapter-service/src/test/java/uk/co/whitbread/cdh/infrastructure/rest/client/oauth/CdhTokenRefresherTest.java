package uk.co.whitbread.cdh.infrastructure.rest.client.oauth;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.cdh.infrastructure.rest.client.oauth.model.CdhToken;

@ExtendWith(MockitoExtension.class)

class CdhTokenRefresherTest {

  @Mock
  private CdhToken cdhToken;
  @Mock
  private OAuthProvider oauthProvider;
  @InjectMocks
  private CdhTokenRefresher cdhTokenRefresher;

  @Test
  void testRefreshValue() {
    // Arrange
    var newValue = "new value";
    when(oauthProvider.getNewBearerToken()).thenReturn(newValue);

    // Act
    cdhTokenRefresher.refreshValue();

    // Assert
    verify(cdhToken).setValue(newValue);
  }
}