package uk.co.whitbread.hotel.register.service.auth0;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.co.whitbread.hotel.register.properties.Auth0Properties;
import uk.co.whitbread.hotel.register.utils.register.Auth0ManagementTransformer;
import uk.co.whitbread.shared.auth.exception.AuthServiceException;
import uk.co.whitbread.shared.auth.model.Auth0User;
import uk.co.whitbread.shared.auth.service.ManagementService;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import uk.co.whitbread.shared.auth.exception.Auth0ApiException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class Auth0BusinessServiceTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ManagementService mockAuthManagementService;
    @Spy
    private Auth0Properties auth0Properties;
    @Spy
    private Auth0ManagementTransformer auth0ManagementTransformer;

    @Spy
    @InjectMocks
    private Auth0BusinessService sut;

    @Test
    void saveBusinessUserInAuth0_shouldMakeRequest() {
        //Given
        doNothing().when(mockAuthManagementService).checkUserNotAlreadyInAuth0(anyString());
        when(mockAuthManagementService.createUser(any(Auth0User.class))).thenReturn(new Auth0User());

        //When
        sut.saveUserInAuth0(random(String.class), random(String.class), random(String.class));

        //Then
        verify(mockAuthManagementService).createUser(any(Auth0User.class));
    }

    @Test
    void saveBusinessUserInAuth0_userAlreadyExistsInAuth0() {
        //Given
        doThrow(AuthServiceException.class).when(mockAuthManagementService).checkUserNotAlreadyInAuth0(anyString());

        //When
        sut.saveUserInAuth0(random(String.class), random(String.class), random(String.class));

        //Then
        verify(mockAuthManagementService, times(0)).createUser(any(Auth0User.class));
    }

    @Test
    void updateUserInAuth0_shouldDelegateToManagementService() {
        //Given
        String userId = random(String.class);
        Auth0User user = new Auth0User();

        //When
        sut.updateUserInAuth0(userId, user);

        //Then
        verify(mockAuthManagementService).updateUser(userId, user);
    }

    @Test
    void updateUserInAuth0_shouldCatchAuth0ApiException() {
        //Given
        String userId = random(String.class);
        Auth0User user = new Auth0User();
        doThrow(Auth0ApiException.class).when(mockAuthManagementService)
            .updateUser(anyString(), any(Auth0User.class));

        //When
        sut.updateUserInAuth0(userId, user);

        //Then
        verify(mockAuthManagementService).updateUser(userId, user);
    }
}
