package uk.co.whitbread.company.employee.service;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.company.employee.utils.Auth0ManagementTransformer.EMPLOYEE_STATUS_LABEL;

import java.util.Map;
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
import uk.co.whitbread.company.employee.model.Employee;
import uk.co.whitbread.company.employee.model.EmployeeStatus;
import uk.co.whitbread.company.employee.properties.Auth0Properties;
import uk.co.whitbread.company.employee.utils.Auth0ManagementTransformer;
import uk.co.whitbread.shared.auth.exception.AuthServiceException;
import uk.co.whitbread.shared.auth.model.Auth0User;
import uk.co.whitbread.shared.auth.service.ManagementService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class Auth0ServiceTest {

    private static final String OLD_EMAIL_TEST = "oldEmail@test.com";
    private static final String NEW_EMAIL_TEST = "newEmail@test.com";

    @Mock
    private ManagementService mockAuthManagementService;
    @Mock
    private Auth0User mockUser;

    @Spy
    private Auth0Properties auth0Properties = new Auth0Properties();
    @Spy
    private Auth0ManagementTransformer auth0ManagementTransformer = new Auth0ManagementTransformer(auth0Properties);

    @Spy
    @InjectMocks
    private Auth0Service sut;

    @BeforeEach
    void setup() {
        when(auth0Properties.isLazyMigrationEnabled()).thenReturn(true);
        when(mockUser.getId()).thenReturn("auth0|123456");
    }

    @Test
    void saveCdhBusinessUserInAuth0_shouldMakeRequest() {
        //Given
        doNothing().when(mockAuthManagementService).checkUserNotAlreadyInAuth0(anyString());
        when(mockAuthManagementService.createUser(any(Auth0User.class))).thenReturn(mockUser);

        //When
        sut.saveCdhBusinessUserInAuth0(random(String.class), random(String.class), random(String.class), random(String.class));

        //Then
        verify(mockAuthManagementService).createUser(any(Auth0User.class));
    }

    @Test
    void saveCdhBusinessUserInAuth0_userAlreadyExistsInAuth0() {
        //Given
        doThrow(AuthServiceException.class).when(mockAuthManagementService).checkUserNotAlreadyInAuth0(anyString());

        //When
        sut.saveCdhBusinessUserInAuth0(random(String.class), random(String.class), random(String.class), random(String.class));

        //Then
        verify(mockAuthManagementService, times(0)).createUser(any(Auth0User.class));
    }

    @Test
    void changeEmail_shouldUpdateEmailInAuth0() {
        //Given
        when(mockAuthManagementService.getUser(anyString())).thenReturn(Optional.of(mockUser));
        when(mockAuthManagementService.updateUser(anyString(), any(Auth0User.class))).thenReturn(mockUser);

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
        verify(mockAuthManagementService, times(0)).updateUser(anyString(), any(Auth0User.class));
    }

    @Test
    void changeEmployeeStatus_shouldUpdateEmployeeStatusInAuth0() {
        //Given
        when(mockAuthManagementService.getUser(anyString())).thenReturn(Optional.of(mockUser));
        when(mockAuthManagementService.updateUser(anyString(), any(Auth0User.class))).thenReturn(mockUser);

        //When
        sut.updateAppMetadata(random(String.class), Map.of(EMPLOYEE_STATUS_LABEL, random(EmployeeStatus.class)));

        //Then
        verify(mockAuthManagementService).updateUser(anyString(), any(Auth0User.class));
    }

    @Test
    void changeEmployeeStatus_userNotExistInAuth0() {
        //Given
        when(mockAuthManagementService.getUser(anyString())).thenReturn(Optional.empty());

        //When
        sut.updateAppMetadata(random(String.class), Map.of(EMPLOYEE_STATUS_LABEL, random(EmployeeStatus.class)));

        //Then
        verify(mockAuthManagementService, times(0)).updateUser(anyString(), any(Auth0User.class));
    }

    @Test
    void changeEmployeeStatus_lazyMigrationDisabled() {
        //Given
        when(auth0Properties.isLazyMigrationEnabled()).thenReturn(false);

        //When
        sut.updateAppMetadata(random(String.class), Map.of(EMPLOYEE_STATUS_LABEL, random(EmployeeStatus.class)));

        //Then
        verify(mockAuthManagementService, times(0)).updateUser(anyString(), any(Auth0User.class));
    }

    @Test
    void updateUserDetailsInAuth0_shouldUpdateEmailInAuth0() {
        //Given
        Employee oldUser = new Employee();
        Employee updatedUser = new Employee();
        oldUser.setEmailAddress(OLD_EMAIL_TEST);
        updatedUser.setEmailAddress(NEW_EMAIL_TEST);
        when(mockAuthManagementService.getUser(oldUser.getEmailAddress())).thenReturn(Optional.of(mockUser));
        when(mockAuthManagementService.updateUser(anyString(), any(Auth0User.class))).thenReturn(mockUser);

        //When
        sut.updateUserDetailsInAuth0(oldUser, updatedUser);

        //Then
        verify(mockAuthManagementService).updateUser(anyString(), any(Auth0User.class));
    }

    @Test
    void updateUserDetailsInAuth0_shouldNotUpdateEmailInAuth0() {
        //Given
        Employee oldUser = new Employee();
        Employee updatedUser = new Employee();
        oldUser.setEmailAddress(OLD_EMAIL_TEST);
        updatedUser.setEmailAddress(OLD_EMAIL_TEST);

        //When
        sut.updateUserDetailsInAuth0(oldUser, updatedUser);

        //Then
        verify(mockAuthManagementService, times(0)).updateUser(anyString(), any(Auth0User.class));
    }

    @Test
    void updateUserDetailsInAuth0_shouldUpdateEmployeeStatusInAuth0() {
        //Given
        Employee oldUser = new Employee();
        Employee updatedUser = new Employee();
        oldUser.setEmailAddress(OLD_EMAIL_TEST);
        oldUser.setEmployeeStatus(EmployeeStatus.ACTIVE);
        updatedUser.setEmployeeStatus(EmployeeStatus.DEACTIVATED);
        when(mockAuthManagementService.getUser(oldUser.getEmailAddress())).thenReturn(Optional.of(mockUser));
        when(mockAuthManagementService.updateUser(anyString(), any(Auth0User.class))).thenReturn(mockUser);

        //When
        sut.updateUserDetailsInAuth0(oldUser, updatedUser);

        //Then
        verify(mockAuthManagementService).updateUser(anyString(), any(Auth0User.class));
    }

    @Test
    void updateUserDetailsInAuth0_shouldNotUpdateEmployeeStatusInAuth0() {
        //Given
        Employee oldUser = new Employee();
        Employee updatedUser = new Employee();
        oldUser.setEmailAddress(OLD_EMAIL_TEST);
        oldUser.setEmployeeStatus(EmployeeStatus.ACTIVE);
        updatedUser.setEmployeeStatus(EmployeeStatus.ACTIVE);

        //When
        sut.updateUserDetailsInAuth0(oldUser, updatedUser);

        //Then
        verify(mockAuthManagementService, times(0)).updateUser(anyString(), any(Auth0User.class));
    }

    @Test
    void updateUserDetailsInAuth0_shouldUpdateEmailAndEmployeeStatusInAuth0() {
        //Given
        Employee oldUser = new Employee();
        Employee updatedUser = new Employee();
        oldUser.setEmailAddress(OLD_EMAIL_TEST);
        oldUser.setEmployeeStatus(EmployeeStatus.ACTIVE);
        updatedUser.setEmailAddress(NEW_EMAIL_TEST);
        updatedUser.setEmployeeStatus(EmployeeStatus.DEACTIVATED);
        when(mockAuthManagementService.getUser(oldUser.getEmailAddress())).thenReturn(Optional.of(mockUser));
        when(mockAuthManagementService.updateUser(anyString(), any(Auth0User.class))).thenReturn(mockUser);

        //When
        sut.updateUserDetailsInAuth0(oldUser, updatedUser);

        //Then
        verify(mockAuthManagementService, times(2)).updateUser(anyString(), any(Auth0User.class));
    }

    @Test
    void updateUserDetailsInAuth0_noUpdateInAuth0() {
        //Given
        Employee oldUser = new Employee();
        Employee updatedUser = new Employee();
        oldUser.setEmailAddress(OLD_EMAIL_TEST);
        oldUser.setEmployeeStatus(EmployeeStatus.ACTIVE);
        updatedUser.setEmailAddress(OLD_EMAIL_TEST);
        updatedUser.setEmployeeStatus(EmployeeStatus.ACTIVE);

        //When
        sut.updateUserDetailsInAuth0(oldUser, updatedUser);

        //Then
        verify(mockAuthManagementService, times(0)).updateUser(anyString(), any(Auth0User.class));
    }

    @Test
    void deleteUser_shouldDeleteUserInAuth0() {
        //Given
        when(mockAuthManagementService.getUser(OLD_EMAIL_TEST)).thenReturn(Optional.of(mockUser));
        doNothing().when(mockAuthManagementService).deleteUser(anyString());

        //When
        sut.deleteUser(OLD_EMAIL_TEST);

        //Then
        verify(mockAuthManagementService).deleteUser(anyString());
    }

    @Test
    void deleteUser_userNotExistInAuth0() {
        //Given
        when(mockAuthManagementService.getUser(anyString())).thenReturn(Optional.empty());

        //When
        sut.deleteUser(OLD_EMAIL_TEST);

        //Then
        verify(mockAuthManagementService, times(0)).deleteUser(anyString());
    }

    @Test
    void deleteUser_lazyMigrationDisabled() {
        //Given
        when(auth0Properties.isLazyMigrationEnabled()).thenReturn(false);

        //When
        sut.deleteUser(OLD_EMAIL_TEST);

        //Then
        verify(mockAuthManagementService, times(0)).deleteUser(anyString());
    }
}
