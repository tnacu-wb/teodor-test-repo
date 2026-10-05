package uk.co.whitbread.company.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.shared.auth.constants.ErrorCodes.UPDATE_USER_EMAIL_ERROR_CODE;

import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.co.whitbread.company.properties.Auth0Properties;
import uk.co.whitbread.shared.auth.exception.Auth0ApiException;
import uk.co.whitbread.shared.auth.exception.AuthServiceException;
import uk.co.whitbread.shared.auth.model.Auth0User;
import uk.co.whitbread.shared.auth.service.ManagementService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class Auth0ServiceTest {

    private static final String OLD_EMAIL_TEST = "oldEmail@test.com";
    private static final String NEW_EMAIL_TEST = "newEmail@test.com";
    private final EnhancedRandom random = EnhancedRandomBuilder.aNewEnhancedRandom();

    @Mock
    private ManagementService mockAuthManagementService;
    @Spy
    private Auth0Properties auth0Properties = new Auth0Properties();

    @Spy
    @InjectMocks
    private Auth0Service sut;

    @BeforeEach
    void setup() {
        when(auth0Properties.isLazyMigrationEnabled()).thenReturn(true);
    }

    @Test
    void changeEmail_shouldUpdateEmailInAuth0() {
        //Given
        when(mockAuthManagementService.getUser(anyString())).thenReturn(Optional.of(random(Auth0User.class)));

        //When
        sut.changeEmail(random(String.class), random(String.class));

        //Then
        verify(mockAuthManagementService).updateUser(anyString(), any(Auth0User.class));
    }

    @Test
    void changeEmail_userNotExistInAuth0() {
        //Given
        when(mockAuthManagementService.getUser(anyString())).thenReturn(Optional.empty());

        //When
        sut.changeEmail(random(String.class), random(String.class));

        //Then
        verify(mockAuthManagementService, never()).updateUser(anyString(), any(Auth0User.class));
    }

    @Test
    void changeEmail_shouldThrowAuthServiceException() {
        //Given
        when(mockAuthManagementService.getUser(anyString())).thenReturn(Optional.of(random(Auth0User.class)));
        doThrow(Auth0ApiException.class).when(mockAuthManagementService).updateUser(anyString(), any(Auth0User.class));

        //Then
        assertThatThrownBy(() -> sut.changeEmail(random(String.class), random(String.class)))
                .isInstanceOf(AuthServiceException.class)
                .hasFieldOrPropertyWithValue("errorCode", UPDATE_USER_EMAIL_ERROR_CODE.getCode());
    }

    @Test
    void changeEmail_lazyMigrationDisabled() {
        //Given
        when(auth0Properties.isLazyMigrationEnabled()).thenReturn(false);

        //When
        sut.changeEmail(random(String.class), random(String.class));

        //Then
        verify(mockAuthManagementService, never()).updateUser(anyString(), any(Auth0User.class));
    }

    @Test
    void updateUserDetailsInAuth0_shouldUpdateEmailInAuth0() {
        //Given
        when(mockAuthManagementService.getUser(OLD_EMAIL_TEST)).thenReturn(Optional.of(random(Auth0User.class)));

        //When
        sut.updateUserDetailsInAuth0(OLD_EMAIL_TEST, NEW_EMAIL_TEST);

        //Then
        verify(mockAuthManagementService).updateUser(anyString(), any(Auth0User.class));
    }

    @Test
    void updateUserDetailsInAuth0_shouldNotUpdateEmailInAuth0() {
        //When
        sut.updateUserDetailsInAuth0(OLD_EMAIL_TEST, OLD_EMAIL_TEST);

        //Then
        verify(mockAuthManagementService, never()).updateUser(anyString(), any(Auth0User.class));
    }

    private <T> T random(Class<T> type) {
        return random.nextObject(type);
    }
}
