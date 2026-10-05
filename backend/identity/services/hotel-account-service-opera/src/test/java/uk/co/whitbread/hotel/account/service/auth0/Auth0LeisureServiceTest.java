package uk.co.whitbread.hotel.account.service.auth0;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.hotel.account.fixture.CustomerFixture.createCustomer;
import static uk.co.whitbread.hotel.account.fixture.CustomerRequestFixture.createCustomerRequest;

import java.util.Optional;
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
import uk.co.whitbread.hotel.account.exceptions.Auth0SimultaneousUpdateException;
import uk.co.whitbread.hotel.account.exceptions.PasswordCheckException;
import uk.co.whitbread.hotel.account.model.Customer;
import uk.co.whitbread.hotel.account.model.CustomerRequest;
import uk.co.whitbread.hotel.account.model.PasswordResetResponse;
import uk.co.whitbread.hotel.account.properties.Auth0Properties;
import uk.co.whitbread.shared.auth.exception.Auth0ApiException;
import uk.co.whitbread.shared.auth.exception.AuthServiceException;
import uk.co.whitbread.shared.auth.exception.InvalidLoginException;
import uk.co.whitbread.shared.auth.model.Auth0User;
import uk.co.whitbread.shared.auth.model.PasswordResetRequest;
import uk.co.whitbread.shared.auth.service.ManagementService;
import uk.co.whitbread.shared.auth.service.TokenService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class Auth0LeisureServiceTest {

    private static final String OLD_EMAIL = "old_email@test.com";
    private static final String NEW_EMAIL = "new_email@test.com";
    private static final String OLD_PASSWORD = "oldPassword";
    private static final String NEW_PASSWORD = "newPassword";
    private static final String REALM = "realm";

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ManagementService mockAuthManagementService;
    @Mock
    private TokenService tokenService;
    @Spy
    private Auth0Properties auth0Properties = new Auth0Properties();

    @InjectMocks
    private Auth0LeisureService auth0LeisureService;

    private static final Auth0User TEST_AUTH0_USER =
        Auth0User.builder().id("test-user-id").email("test@test.com").build();

    @BeforeEach
    void setup() {
        auth0Properties.setLazyMigrationEnabled(true);
        when(auth0Properties.getB2cConnection()).thenReturn(REALM);
    }

    @Test
    void changePassword_shouldUpdatePasswordInAuth0() {
        //Given
        when(mockAuthManagementService.getUser(anyString())).thenReturn(Optional.of(TEST_AUTH0_USER));

        //When
        auth0LeisureService.changePassword("test@test.com", "newPassword");

        //Then
        verify(mockAuthManagementService).updateUser(anyString(), any(Auth0User.class));
    }

    @Test
    void changePassword_userNotExistInAuth0() {
        //Given
        when(mockAuthManagementService.getUser(anyString())).thenReturn(Optional.empty());

        //When
        auth0LeisureService.changePassword("test@test.com", "newPassword");

        //Then
        verify(mockAuthManagementService, never()).updateUser(anyString(), any(Auth0User.class));
    }

    @Test
    void changePassword_lazyMigrationDisabled() {
        //When
        when(auth0Properties.isLazyMigrationEnabled()).thenReturn(false);
        auth0LeisureService.changePassword("test@test.com", "newPassword");

        //Then
        verify(mockAuthManagementService, never()).updateUser(anyString(), any(Auth0User.class));
    }

    @Test
    void changeEmail_shouldUpdateEmailInAuth0() {
        //Given
        when(mockAuthManagementService.getUser(anyString())).thenReturn(Optional.of(TEST_AUTH0_USER));

        //When
        auth0LeisureService.changeEmail("old@test.com", "new@test.com");

        //Then
        verify(mockAuthManagementService).updateUser(anyString(), any(Auth0User.class));
    }

    @Test
    void changeEmail_userNotExistInAuth0() {
        //Given
        when(mockAuthManagementService.getUser(anyString())).thenReturn(Optional.empty());

        //When
        auth0LeisureService.changeEmail("old@test.com", "new@test.com");

        //Then
        verify(mockAuthManagementService, never()).updateUser(anyString(), any(Auth0User.class));
    }

    @Test
    void changeEmail_lazyMigrationDisabled() {
        //When
        when(auth0Properties.isLazyMigrationEnabled()).thenReturn(false);
        auth0LeisureService.changeEmail("old@test.com", "new@test.com");

        //Then
        verify(mockAuthManagementService, never()).updateUser(anyString(), any(Auth0User.class));
    }

    @Test
    void updateEmailAndPasswordInAuth0_noUpdateInAuth0() {
        //Given
        Customer oldUser = createCustomer(OLD_EMAIL);
        CustomerRequest updatedUser = createCustomerRequest(NEW_EMAIL, NEW_PASSWORD);

        // When/Then
        assertThrows(Auth0SimultaneousUpdateException.class,
                () -> auth0LeisureService.updateEmailOrPasswordInAuth0(oldUser, updatedUser, true));
    }

    @Test
    void updateBARTEmailAndPasswordInAuth0_noUpdateInAuth0() {
        //Given
        Customer oldUser = createCustomer(OLD_EMAIL);
        CustomerRequest updatedUser = createCustomerRequest(NEW_EMAIL, NEW_PASSWORD);

        // When/Then
        assertThrows(Auth0SimultaneousUpdateException.class,
                () -> auth0LeisureService.updateEmailOrPasswordInAuth0(oldUser, updatedUser, false));
    }

    @Test
    void updateEmailAndPasswordInAuth0_uppercaseEmailNoUpdateInAuth0() {
        //Given
        Customer oldUser = createCustomer(OLD_EMAIL);
        CustomerRequest updatedUser = createCustomerRequest(OLD_EMAIL.toUpperCase(), null);

        //When
        auth0LeisureService.updateEmailOrPasswordInAuth0(oldUser, updatedUser, true);

        //Then
        verify(mockAuthManagementService, never()).updateUser(anyString(), any(Auth0User.class));
    }

    @Test
    void updateEmailOrPasswordInAuth0_shouldUpdateEmailInAuth0() {
        //Given
        Customer oldUser = createCustomer(OLD_EMAIL);
        CustomerRequest updatedUser = createCustomerRequest(NEW_EMAIL, null);
        when(mockAuthManagementService.getUser(oldUser.getContactDetail().getEmail())).thenReturn(
            Optional.of(TEST_AUTH0_USER));

        //When
        auth0LeisureService.updateEmailOrPasswordInAuth0(oldUser, updatedUser, true);

        //Then
        verify(mockAuthManagementService).updateUser(anyString(), any(Auth0User.class));
    }

    @Test
    void updateEmailOrPasswordInAuth0_shouldUpdatePasswordInAuth0() {
        //Given
        Customer oldUser = createCustomer(OLD_EMAIL);
        CustomerRequest updatedUser = createCustomerRequest(OLD_EMAIL, NEW_PASSWORD);
        when(mockAuthManagementService.getUser(oldUser.getContactDetail().getEmail())).thenReturn(
            Optional.of(TEST_AUTH0_USER));

        //When
        auth0LeisureService.updateEmailOrPasswordInAuth0(oldUser, updatedUser, true);

        //Then
        verify(mockAuthManagementService).updateUser(anyString(), any(Auth0User.class));
        verify(mockAuthManagementService).login(anyString(), anyString(), anyString());
    }

    @Test
    void updateBARTEmailOrPasswordInAuth0_shouldUpdatePasswordInAuth0() {
        //Given
        Customer oldUser = createCustomer(OLD_EMAIL);
        CustomerRequest updatedUser = createCustomerRequest(OLD_EMAIL, NEW_PASSWORD);
        when(mockAuthManagementService.getUser(oldUser.getContactDetail().getEmail())).thenReturn(
            Optional.of(TEST_AUTH0_USER));

        //When
        auth0LeisureService.updateEmailOrPasswordInAuth0(oldUser, updatedUser, false);

        //Then
        verify(mockAuthManagementService).updateUser(anyString(), any(Auth0User.class));
        verify(mockAuthManagementService, never()).login(anyString(), anyString(), anyString());
    }

    @Test
    void updateEmailOrPasswordInAuth0_noUpdateInAuth0() {
        //Given
        Customer oldUser = createCustomer(OLD_EMAIL);
        CustomerRequest updatedUser = createCustomerRequest(OLD_EMAIL, null);

        //When
        auth0LeisureService.updateEmailOrPasswordInAuth0(oldUser, updatedUser, true);

        //Then
        verify(mockAuthManagementService, never()).updateUser(anyString(), any(Auth0User.class));
    }

    @Test
    void updateEmailOrPasswordInAuth0_shouldThrowErrorWhenLoginFails() {
        //Given
        Customer oldUser = createCustomer(OLD_EMAIL);
        CustomerRequest updatedUser = createCustomerRequest(OLD_EMAIL, OLD_PASSWORD);
        when(mockAuthManagementService.getUser(oldUser.getContactDetail().getEmail())).thenReturn(
            Optional.of(TEST_AUTH0_USER));
        doThrow(new InvalidLoginException("bad credentials")).when(mockAuthManagementService)
            .login(anyString(), anyString(), anyString());

        // When/then
        assertThrows(PasswordCheckException.class,
                () -> auth0LeisureService.updateEmailOrPasswordInAuth0(oldUser, updatedUser, true));

    }

    @Test
    void updateBARTEmailOrPasswordInAuth0_shouldThrowErrorWhenLoginFails() {
        //Given
        Customer oldUser = createCustomer(OLD_EMAIL);
        CustomerRequest updatedUser = createCustomerRequest(OLD_EMAIL, OLD_PASSWORD);
        when(mockAuthManagementService.getUser(oldUser.getContactDetail().getEmail())).thenReturn(
            Optional.of(TEST_AUTH0_USER));

        //When
        auth0LeisureService.updateEmailOrPasswordInAuth0(oldUser, updatedUser, false);

        //Then
        verify(mockAuthManagementService).updateUser(anyString(), any(Auth0User.class));
        verify(mockAuthManagementService, never()).login(anyString(), anyString(), anyString());
    }

    @Test
    void rollbackUserUpdate_shouldRollbackPasswordInAuth0() {
        //Given
        Customer oldUser = createCustomer(OLD_EMAIL);
        CustomerRequest updatedUser = createCustomerRequest(OLD_EMAIL, NEW_PASSWORD);
        updatedUser.setPassword(OLD_PASSWORD);
        when(mockAuthManagementService.getUser(oldUser.getContactDetail().getEmail())).thenReturn(
            Optional.of(TEST_AUTH0_USER));

        //When
        auth0LeisureService.rollbackUserUpdate(oldUser, updatedUser, true);

        //Then
        verify(mockAuthManagementService).updateUser(anyString(), any(Auth0User.class));
        verify(mockAuthManagementService).login(anyString(), anyString(), anyString());
    }

    @Test
    void rollbackBARTUserUpdate_shouldRollbackPasswordInAuth0() {
        //Given
        Customer oldUser = createCustomer(OLD_EMAIL);
        CustomerRequest updatedUser = createCustomerRequest(OLD_EMAIL, NEW_PASSWORD);
        updatedUser.setPassword(OLD_PASSWORD);
        when(mockAuthManagementService.getUser(oldUser.getContactDetail().getEmail())).thenReturn(
            Optional.of(TEST_AUTH0_USER));

        //When
        auth0LeisureService.rollbackUserUpdate(oldUser, updatedUser, false);

        //Then
        verify(mockAuthManagementService).updateUser(anyString(), any(Auth0User.class));
        verify(mockAuthManagementService, never()).login(anyString(), anyString(), anyString());
    }

    @Test
    void rollbackUserUpdate_shouldRollbackEmailInAuth0() {
        //Given
        Customer oldUser = createCustomer(OLD_EMAIL);
        CustomerRequest updatedUser = createCustomerRequest(NEW_EMAIL, null);
        when(mockAuthManagementService.getUser(updatedUser.getContactDetail().getEmail())).thenReturn(
            Optional.of(TEST_AUTH0_USER));

        //When
        auth0LeisureService.rollbackUserUpdate(oldUser, updatedUser, true);

        //Then
        verify(mockAuthManagementService).updateUser(anyString(), any(Auth0User.class));
    }

    @Test
    void updateUserInAuth0_shouldThrowAuthServiceException() {
        //Given
        doThrow(Auth0ApiException.class).when(mockAuthManagementService).updateUser(anyString(), any(Auth0User.class));

        //When/Then
        Auth0User auth0User = Auth0User.builder().build();
        assertThrows(AuthServiceException.class,
            () -> auth0LeisureService.updateUserInAuth0("test-id", auth0User));
    }

    @Test
    void deleteUser_shouldDeleteUserInAuth0() {
        //Given
        when(mockAuthManagementService.getUser(OLD_EMAIL)).thenReturn(Optional.of(TEST_AUTH0_USER));

        //When
        auth0LeisureService.deleteUser(OLD_EMAIL);

        //Then
        verify(mockAuthManagementService).deleteUser(anyString());
    }

    @Test
    void deleteUser_userNotExistInAuth0() {
        //Given
        when(mockAuthManagementService.getUser(anyString())).thenReturn(Optional.empty());

        //When
        auth0LeisureService.deleteUser(OLD_EMAIL);

        //Then
        verify(mockAuthManagementService, never()).deleteUser(anyString());
    }

    @Test
    void deleteUser_lazyMigrationDisabled() {
        //When
        when(auth0Properties.isLazyMigrationEnabled()).thenReturn(false);
        auth0LeisureService.deleteUser(OLD_EMAIL);

        //Then
        verify(mockAuthManagementService, never()).deleteUser(anyString());
    }

    @Test
    void getPasswordResetUrl_shouldReturnPasswordResetUrl() {
        // Given
        String email = "test@test.com";
        String userId = "auth0|123";
        String expectedUrl = "https://auth0.com/reset?ticket=abc123";

        Auth0User user = Auth0User.builder().id(userId).email(email).build();
        when(mockAuthManagementService.getUser(email)).thenReturn(Optional.of(user));
        when(mockAuthManagementService.requestPasswordChange(any(PasswordResetRequest.class))).thenReturn(expectedUrl);

        // When
        PasswordResetResponse response = auth0LeisureService.getPasswordResetUrl(email);

        // Then
        assertNotNull(response);
        assertEquals(expectedUrl, response.getPasswordResetUrl());
    }

    @Test
    void getPasswordResetUrl_shouldThrowAuthServiceException_whenUserNotFound() {
        // Given
        String email = "notfound@test.com";
        when(mockAuthManagementService.getUser(email)).thenReturn(Optional.empty());

        // When/Then
        AuthServiceException exception = assertThrows(AuthServiceException.class,
            () -> auth0LeisureService.getPasswordResetUrl(email));
        assertEquals("User not found in Auth0", exception.getMessage());
    }

    @Test
    void getPasswordResetUrl_shouldThrowAuthServiceException_whenAuth0ExceptionOccurs() {
        // Given
        String email = "test@test.com";
        String userId = "auth0|123";

        Auth0User user = Auth0User.builder().id(userId).email(email).build();
        when(mockAuthManagementService.getUser(email)).thenReturn(Optional.of(user));
        when(mockAuthManagementService.requestPasswordChange(any(PasswordResetRequest.class)))
            .thenThrow(new Auth0ApiException("Auth0 error", 500, "error", "Auth0 error"));

        // When/Then
        assertThrows(Auth0ApiException.class,
            () -> auth0LeisureService.getPasswordResetUrl(email));
    }
}
