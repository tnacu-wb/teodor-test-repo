package uk.co.whitbread.hotel.account.service.auth0;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.UsersEntity;
import com.auth0.json.mgmt.users.User;
import com.auth0.net.Request;
import com.auth0.net.Response;
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
import uk.co.whitbread.hotel.account.properties.Auth0Properties;
import uk.co.whitbread.hotel.account.utils.auth.Auth0ManagementTransformer;
import uk.co.whitbread.shared.auth.exception.AuthServiceException;
import uk.co.whitbread.shared.auth.exception.InvalidLoginException;
import uk.co.whitbread.shared.auth.service.EncryptionService;
import uk.co.whitbread.shared.auth.service.ManagementService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class Auth0LeisureServiceTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ManagementService mockAuthManagementService;
    @Mock
    private ManagementAPI mockManagementAPI;
    @Mock
    private UsersEntity mockUsersEntity;
    @Mock
    private Request<User> mockRequestUser;
    @Mock
    private Response<User> mockResponse;
    @Mock
    private User mockUser;
    @Mock
    private EncryptionService mockEncryptionService;
    @Spy
    private Auth0Properties auth0Properties;
    @Spy
    private Auth0ManagementTransformer auth0ManagementTransformer;

    @InjectMocks
    private Auth0LeisureService sut;

    @BeforeEach
    public void setup() {
        when(mockAuthManagementService.retrieveManagementAPI()).thenReturn(mockManagementAPI);
        when(mockManagementAPI.users()).thenReturn(mockUsersEntity);
        when(mockUsersEntity.create(any(User.class))).thenReturn(mockRequestUser);
    }

    @Test
    public void saveLeisureUserInAuth0_shouldMakeRequest() throws Exception {
        //Given
        when(auth0Properties.isLazyMigrationEnabled()).thenReturn(true);
        doNothing().when(mockAuthManagementService).checkUserNotAlreadyInAuth0(anyString());
        when(mockEncryptionService.writeSecuredMessage(anyString())).thenReturn(random(String.class));
        when(mockRequestUser.execute()).thenReturn(mockResponse);
        when(mockResponse.getBody()).thenReturn(mockUser);

        //When
        sut.saveUserInAuth0(random(String.class), random(String.class), random(String.class));

        //Then
        verify(mockEncryptionService).writeSecuredMessage(anyString());
        verify(mockRequestUser).execute();
    }

    @Test
    public void saveLeisureUserInAuth0_userAlreadyExistsInAuth0() throws Exception {
        //Given
        when(auth0Properties.isLazyMigrationEnabled()).thenReturn(true);
        doThrow(AuthServiceException.class).when(mockAuthManagementService).checkUserNotAlreadyInAuth0(anyString());

        //When
        sut.saveUserInAuth0(random(String.class), random(String.class), random(String.class));

        //Then
        verify(mockEncryptionService, times(0)).writeSecuredMessage(anyString());
        verify(mockRequestUser, times(0)).execute();
    }

    @Test
    public void saveLeisureUserInAuth0_encryptionFails() throws Exception {
        //Given
        when(auth0Properties.isLazyMigrationEnabled()).thenReturn(true);
        doThrow(InvalidLoginException.class).when(mockEncryptionService).writeSecuredMessage(anyString());

        //When
        sut.saveUserInAuth0(random(String.class), random(String.class), random(String.class));

        //Then
        verify(mockRequestUser, times(0)).execute();
    }

    @Test
    public void saveLeisureUserInAuth0_lazyMigrationDisabled() throws Exception {
        //Given
        when(auth0Properties.isLazyMigrationEnabled()).thenReturn(false);

        //When
        sut.saveUserInAuth0(random(String.class), random(String.class), random(String.class));

        //Then
        verify(mockAuthManagementService, times(0)).retrieveManagementAPI();
        verify(mockEncryptionService, times(0)).writeSecuredMessage(anyString());
        verify(mockRequestUser, times(0)).execute();
    }
}