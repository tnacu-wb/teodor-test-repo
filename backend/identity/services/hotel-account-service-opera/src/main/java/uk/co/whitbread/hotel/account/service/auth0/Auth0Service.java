package uk.co.whitbread.hotel.account.service.auth0;

import static uk.co.whitbread.common.exceptions.ErrorCodes.AUTH0_GENERIC_ERROR_CODE;
import static uk.co.whitbread.hotel.account.utils.Utils.sanitizeInputString;
import static uk.co.whitbread.shared.auth.constants.ErrorCodes.UPDATE_USER_EMAIL_ERROR_CODE;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RequiredArgsConstructor
public abstract class Auth0Service {

    public static final String RESET_PASSWORD_TOKEN_FIELD = "reset_password_token";
    public static final String RESET_PASSWORD_TOKEN_EXPIRY_FIELD = "reset_password_token_expiry";
    public static final String APP_METADATA = "app_metadata";

    protected final Auth0Properties auth0Properties;

    private final TokenService tokenService;

    public abstract ManagementService getAuthManagementService();

    public void deleteUser(String email) {
        Optional<String> optionalUserId = getAuth0UserId(email);
        if (optionalUserId.isEmpty()) {
            return;
        }
        String userId = optionalUserId.get();
        log.debug("Deleting user with email {} from Auth0", email);
        getAuthManagementService().deleteUser(userId);
    }

    public Optional<Auth0User> getAuth0User(String email) {
        if (!auth0Properties.isLazyMigrationEnabled()) {
            return Optional.empty();
        }
        try {
            final ManagementService authManagementService = getAuthManagementService();
            Optional<Auth0User> user = authManagementService.getUser(email.toLowerCase());
            if (user.isEmpty()) {
                log.info("User doesn't exist in Auth0 database {}",
                    authManagementService.getManagementProperties().getConnection());
            }
            return user;
        } catch (AuthServiceException e) {
            log.error("Error while retrieving user from Auth0", e);
            return Optional.empty();
        }
    }

    public PasswordResetResponse getPasswordResetUrl(String email) {
        String userId = getAuth0User(email)
            .map(Auth0User::getId)
            .orElseThrow(() -> new AuthServiceException("User not found in Auth0"));
        String passwordResetUrl = generatePasswordResetUrl(userId);
        return PasswordResetResponse.builder().passwordResetUrl(passwordResetUrl).build();
    }

    private String generatePasswordResetUrl(String userId) {
        PasswordResetRequest request = PasswordResetRequest.builder()
            .userId(userId)
            .clientId(auth0Properties.getClientId())
            .build();
        return getAuthManagementService().requestPasswordChange(request);
    }

    public List<Auth0User> findUserByResetToken(String resetToken) {
        String query = APP_METADATA + "." + RESET_PASSWORD_TOKEN_FIELD + ":\"" + resetToken + "\"";

        try {
            return getAuthManagementService().listUsers(query);
        } catch (Exception e) {
            log.error("Auth0Exception for validate reset key", e);
            throw new AuthServiceException("Error while searching for user by reset token", e)
                .withErrorCode(AUTH0_GENERIC_ERROR_CODE.getCode());
        }
    }

    public void changePassword(String email, String newPassword) {
        Optional<String> optionalUserId = getAuth0UserId(email);
        if (optionalUserId.isEmpty()) {
            return;
        }
        String userId = optionalUserId.get();
        Auth0User updatedUser = Auth0User.builder().password(newPassword).build();
        log.debug("Updating password for {} in Auth0", email);
        updateUserInAuth0(userId, updatedUser);
    }

    public void changeEmail(String email, String newEmail) {
        Optional<String> optionalUserId = getAuth0UserId(email);
        if (optionalUserId.isEmpty()) {
            return;
        }
        String userId = optionalUserId.get();
        Auth0User updatedUser = Auth0User.builder()
            .email(newEmail)
            .name(newEmail)
            .emailVerified(true)
            .build();
        log.debug("Updating email for {} in Auth0", sanitizeInputString(email));
        updateUserInAuth0(userId, updatedUser);
    }

    public void updateEmailOrPasswordInAuth0(Customer oldUser, CustomerRequest updatedUser, boolean isCdhActive) {
        boolean updateEmail = isEmailChanged(oldUser, updatedUser);
        boolean updatePassword = isPasswordChanged(updatedUser.getPassword(), updatedUser.getNewPassword(),
                oldUser.getContactDetail().getEmail(), isCdhActive);
        if (!updateEmail && !updatePassword) {
            log.debug("Neither the email nor the password have changed, no update needed in Auth0");
            return;
        }
        if (updateEmail && updatePassword) {
            throw new Auth0SimultaneousUpdateException("Cannot update both the email and the password in the same call");
        }
        if (updatePassword) {
            changePassword(oldUser.getContactDetail().getEmail(), updatedUser.getNewPassword());
            return;
        }
        changeEmail(oldUser.getContactDetail().getEmail(), updatedUser.getContactDetail().getEmail().toLowerCase());
    }

    public void updateUserInAuth0(String userId, Auth0User updatedUser) {
        try {
            getAuthManagementService().updateUser(userId, updatedUser);
        } catch (Auth0ApiException e) {
            log.error("Auth0Exception for user", e);
            throw new AuthServiceException("Error while trying to update user", e)
                    .withErrorCode(UPDATE_USER_EMAIL_ERROR_CODE.getCode());
        }
    }

    public void rollbackUserUpdate(Customer oldUser, CustomerRequest updatedUser, boolean isCdhActive) {
        String oldPassword = updatedUser.getPassword();
        boolean isEmailChanged = isEmailChanged(oldUser, updatedUser);
        boolean isPasswordChanged = isPasswordChanged(updatedUser.getNewPassword(), updatedUser.getPassword(),
                oldUser.getContactDetail().getEmail(), isCdhActive);
        if (isEmailChanged) {
            String oldEmail = oldUser.getContactDetail().getEmail();
            String newEmail = updatedUser.getContactDetail().getEmail();
            oldUser.getContactDetail().setEmail(newEmail);
            updatedUser.getContactDetail().setEmail(oldEmail);
            log.debug("Rollback user update in Auth0 for {}",
                sanitizeInputString(oldUser.getContactDetail().getEmail()));
            changeEmail(oldUser.getContactDetail().getEmail(), updatedUser.getContactDetail().getEmail().toLowerCase());
        } else if (isPasswordChanged) {
            updatedUser.setNewPassword(oldPassword);
            log.debug("Rollback user update in Auth0 for {}",
                sanitizeInputString(oldUser.getContactDetail().getEmail()));
            changePassword(oldUser.getContactDetail().getEmail(), updatedUser.getNewPassword());
        }
    }

    protected Optional<String> getAuth0UserId(String email) {
        final Optional<Auth0User> user = getAuth0User(email);
        if (user.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(user.get().getId());
    }

    private boolean isEmailChanged(Customer oldUser, CustomerRequest updatedUser) {
        return Optional.ofNullable(updatedUser.getContactDetail()).isPresent() &&
                Optional.ofNullable(updatedUser.getContactDetail().getEmail()).isPresent() &&
                !updatedUser.getContactDetail().getEmail().equalsIgnoreCase(oldUser.getContactDetail().getEmail());
    }

    private boolean isPasswordChanged(String currentPassword, String newPassword, String email, boolean isCdhActive) {
        boolean isPasswordChanged = Optional.ofNullable(newPassword).isPresent() &&
                Optional.ofNullable(currentPassword).isPresent() &&
                !newPassword.equals(currentPassword);
         if (isPasswordChanged && isCdhActive) {
             verifyCurrentPassword(email, currentPassword);
         }
         return isPasswordChanged;
    }

    private void verifyCurrentPassword(String email, String password) {
        try {
            getAuthManagementService().login(email, password, getAuth0Connection());
        } catch (InvalidLoginException e) {
            log.warn(e.getMessage());
            throw new PasswordCheckException("Existing password doesn't match records");
        }
    }

    abstract String getAuth0Connection();
}
