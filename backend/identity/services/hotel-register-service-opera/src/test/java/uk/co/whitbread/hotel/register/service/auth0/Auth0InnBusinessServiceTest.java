package uk.co.whitbread.hotel.register.service.auth0;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.co.whitbread.hotel.register.utils.register.Auth0ManagementTransformer;
import uk.co.whitbread.shared.auth.exception.AuthServiceException;
import uk.co.whitbread.shared.auth.model.Auth0User;
import uk.co.whitbread.shared.auth.service.ManagementService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class Auth0InnBusinessServiceTest {

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private ManagementService mockAuthManagementService;
  @Spy
  private Auth0ManagementTransformer auth0ManagementTransformer;

  @Spy
  @InjectMocks
  private Auth0InnBusinessService sut;

  @Test
  void saveUserInAuth0_shouldMakeRequest() {
    // Given
    doNothing().when(mockAuthManagementService).checkUserNotAlreadyInAuth0(anyString());
    when(mockAuthManagementService.createUser(any(Auth0User.class))).thenReturn(new Auth0User());

    // When
    sut.saveUserInAuth0(random(String.class), random(String.class), random(String.class), random(String.class));

    // Then
    verify(mockAuthManagementService).createUser(any(Auth0User.class));
  }

  @Test
  void saveUserInAuth0_userAlreadyExistsInAuth0() {
    // Given
    doThrow(AuthServiceException.class).when(mockAuthManagementService).checkUserNotAlreadyInAuth0(anyString());

    // When
    sut.saveUserInAuth0(random(String.class), random(String.class), random(String.class), random(String.class));

    // Then
    verify(mockAuthManagementService, times(0)).createUser(any(Auth0User.class));
  }

}
