package uk.co.whitbread.company.employee.service;

import static uk.co.whitbread.company.employee.utils.Auth0ManagementTransformer.COMPANY_ACCOUNT_ID_LABEL;
import static uk.co.whitbread.company.employee.utils.Auth0ManagementTransformer.EMPLOYEE_ACCOUNT_ID_LABEL;
import static uk.co.whitbread.company.employee.utils.Auth0ManagementTransformer.EMPLOYEE_STATUS_LABEL;
import static uk.co.whitbread.company.employee.utils.SanitizingUtils.sanitize;
import static uk.co.whitbread.shared.auth.constants.ErrorCodes.UPDATE_USER_EMAIL_ERROR_CODE;

import uk.co.whitbread.shared.auth.exception.Auth0ApiException;
import uk.co.whitbread.shared.auth.model.Auth0User;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.company.employee.model.Employee;
import uk.co.whitbread.company.employee.model.EmployeeStatus;
import uk.co.whitbread.company.employee.properties.Auth0Properties;
import uk.co.whitbread.company.employee.utils.Auth0ManagementTransformer;
import uk.co.whitbread.shared.auth.exception.AuthServiceException;
import uk.co.whitbread.shared.auth.service.ManagementService;

@Slf4j
@Service
public class Auth0Service {

    private static final String CREATE_USER_IN_AUTH0_LOG_MESSAGE = "Creating user {} in Auth0";

    private final Auth0Properties auth0Properties;
    private final ManagementService authManagementService;
    private final Auth0ManagementTransformer auth0ManagementTransformer;

    public Auth0Service(Auth0Properties auth0Properties, ManagementService authManagementService,
                        Auth0ManagementTransformer auth0ManagementTransformer) {
        this.auth0Properties = auth0Properties;
        this.authManagementService = authManagementService;
        this.auth0ManagementTransformer = auth0ManagementTransformer;
    }

    public void saveCdhBusinessUserInAuth0(String email, String password, String companyAccountId,
        String employeeAccountId) throws Auth0ApiException {

        try {
            authManagementService.checkUserNotAlreadyInAuth0(email);
        } catch (AuthServiceException e) {
            log.warn("User already exists in Auth0");
            return;
        }
        Auth0User auth0User = auth0ManagementTransformer.transform(email, password,
            Map.of(EMPLOYEE_STATUS_LABEL, EmployeeStatus.ACTIVE,
                COMPANY_ACCOUNT_ID_LABEL, companyAccountId,
                EMPLOYEE_ACCOUNT_ID_LABEL, employeeAccountId));
        log.debug(CREATE_USER_IN_AUTH0_LOG_MESSAGE, sanitize(email));
        createUserInAuth0(auth0User);
    }

    public void updateAppMetadata(String email, Map<String, Object> appMetadata) {
        if (!auth0Properties.isLazyMigrationEnabled()) {
            return;
        }
        Optional<Auth0User> optionalUser = authManagementService.getUser(email);
        if (optionalUser.isEmpty()) {
            log.warn("User doesn't exist in auth0");
            return;
        }
        Auth0User user = new Auth0User();
        user.setAppMetadata(appMetadata);
        log.debug("Updating app metadata for {} in Auth0", sanitize(email));
        updateUserInAuth0(optionalUser.get().getId(), user);
    }

    public void changeEmail(String email, String newEmail) {
        Optional<String> optionalUserId = getAuth0UserId(email);
        if (optionalUserId.isEmpty()) {
            return;
        }
        String userId = optionalUserId.get();
        Auth0User updatedUser = new Auth0User();
        updatedUser.setEmail(newEmail);
        updatedUser.setName(newEmail);
        updatedUser.setEmailVerified(true);
        log.debug("Updating email for {} in Auth0", sanitize(email));
        updateUserInAuth0(userId, updatedUser);
    }

    public void deleteUser(String email) throws Auth0ApiException {
        Optional<String> optionalUserId = getAuth0UserId(email);
        if (optionalUserId.isEmpty()) {
            return;
        }
        String userId = optionalUserId.get();
        log.debug("Deleting user with email {} from Auth0", sanitize(email));
        deleteUserInAuth0(userId);
    }

    public void updateUserDetailsInAuth0(Employee oldUser, Employee updatedUser) {
        boolean updateEmail = Optional.ofNullable(updatedUser.getEmailAddress()).isPresent() &&
                !updatedUser.getEmailAddress().equals(oldUser.getEmailAddress());
        boolean updateStatus = Optional.ofNullable(updatedUser.getEmployeeStatus()).isPresent() &&
                !updatedUser.getEmployeeStatus().equals(EmployeeStatus.PURGED) &&
                !updatedUser.getEmployeeStatus().equals(oldUser.getEmployeeStatus());
        if (updateStatus && oldUser.getEmailAddress() != null) {
            updateAppMetadata(oldUser.getEmailAddress(), Map.of(EMPLOYEE_STATUS_LABEL, updatedUser.getEmployeeStatus()));
        }
        if (updateEmail && oldUser.getEmailAddress() != null) {
            changeEmail(oldUser.getEmailAddress(), updatedUser.getEmailAddress());
        }
    }

    private void updateUserInAuth0(String userId, Auth0User updatedUser) {
        try {
            authManagementService.updateUser(userId, updatedUser);
        } catch (Auth0ApiException e) {
            throw new AuthServiceException(
                String.format("Error while trying to update user %s in Auth0", userId), e)
                .withErrorCode(UPDATE_USER_EMAIL_ERROR_CODE.getCode());
        }
    }

    private void createUserInAuth0(Auth0User newUser) throws Auth0ApiException {
        authManagementService.createUser(newUser);
    }

    private void deleteUserInAuth0(String userId) throws Auth0ApiException {
        authManagementService.deleteUser(userId);
    }

    private Optional<String> getAuth0UserId(String email) {
        if (!auth0Properties.isLazyMigrationEnabled()) {
            return Optional.empty();
        }
        Optional<Auth0User> optionalUser = authManagementService.getUser(email);
        if (optionalUser.isEmpty()) {
            log.debug("User {} doesn't exist in Auth0", sanitize(email));
            return Optional.empty();
        }
        String userId = optionalUser.get().getId();
        return Optional.of(userId);
    }
}
