package uk.co.whitbread.company.service;

import static uk.co.whitbread.shared.auth.constants.ErrorCodes.UPDATE_USER_EMAIL_ERROR_CODE;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.company.properties.Auth0Properties;
import uk.co.whitbread.shared.auth.exception.Auth0ApiException;
import uk.co.whitbread.shared.auth.exception.AuthServiceException;
import uk.co.whitbread.shared.auth.model.Auth0User;
import uk.co.whitbread.shared.auth.service.ManagementService;

@Slf4j
@Service
@RequiredArgsConstructor
public class Auth0Service {

    private final Auth0Properties auth0Properties;
    private final ManagementService authManagementService;

    public void changeEmail(String email, String newEmail) {
        Optional<String> optionalUserId = getAuth0UserId(email);
        if (optionalUserId.isEmpty()) {
            return;
        }
        String userId = optionalUserId.get();
        Auth0User updatedUser = Auth0User.builder()
            .email(newEmail)
            .name(newEmail)
            .emailVerified(Boolean.TRUE)
            .build();
        log.debug("Updating email for {} in Auth0", email);
        updateUserInAuth0(userId, updatedUser);
    }

    public void updateUserDetailsInAuth0(String oldEmail, String updatedEmail) {
        boolean updateEmail = Optional.ofNullable(updatedEmail).isPresent() &&
                !updatedEmail.equalsIgnoreCase(oldEmail);
        if (updateEmail) {
            changeEmail(oldEmail, updatedEmail.toLowerCase());
        }
    }

    private void updateUserInAuth0(String userId, Auth0User updatedUser) {
        try {
            authManagementService.updateUser(userId, updatedUser);
        } catch (Auth0ApiException e) {
            log.error("Auth0Exception for user", e);
            throw new AuthServiceException("Error while trying to update user", e)
                    .withErrorCode(UPDATE_USER_EMAIL_ERROR_CODE.getCode());
        }
    }

    private Optional<String> getAuth0UserId(String email) {
        if (!auth0Properties.isLazyMigrationEnabled()) {
            return Optional.empty();
        }
        Optional<Auth0User> optionalUser = authManagementService.getUser(email);
        if (optionalUser.isEmpty()) {
            log.debug("User {} doesn't exist in Auth0", email);
            return Optional.empty();
        }
        String userId = optionalUser.get().getId();
        return Optional.of(userId);
    }
}
