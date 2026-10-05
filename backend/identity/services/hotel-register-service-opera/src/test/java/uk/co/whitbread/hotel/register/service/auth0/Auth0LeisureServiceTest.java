package uk.co.whitbread.hotel.register.service.auth0;

import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class Auth0LeisureServiceTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ManagementService mockAuthManagementService;
    @Spy
    private Auth0Properties auth0Properties;
    @Spy
    private Auth0ManagementTransformer auth0ManagementTransformer;

    @InjectMocks
    private Auth0LeisureService sut;

    @BeforeEach
    void setup() {
        when(mockAuthManagementService.createUser(any(Auth0User.class))).thenReturn(new Auth0User());
    }

    @Test
    void saveLeisureUserInAuth0_shouldMakeRequest() {
        //Given
        doNothing().when(mockAuthManagementService).checkUserNotAlreadyInAuth0(anyString());

        //When
        sut.saveUserInAuth0(random(String.class), random(String.class), random(String.class));

        //Then
        verify(mockAuthManagementService).createUser(any(Auth0User.class));
    }

    @Test
    void saveLeisureUserInAuth0_userAlreadyExistsInAuth0() {
        //Given
        doThrow(AuthServiceException.class).when(mockAuthManagementService).checkUserNotAlreadyInAuth0(anyString());

        //When
        sut.saveUserInAuth0(random(String.class), random(String.class), random(String.class));

        //Then
        verify(mockAuthManagementService, times(0)).createUser(any(Auth0User.class));
    }

}
