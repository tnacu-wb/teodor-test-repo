package uk.co.whitbread.hotel.register.service.auth0;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.shared.auth.exception.Auth0ApiException;
import uk.co.whitbread.shared.auth.model.Auth0User;
import uk.co.whitbread.shared.auth.service.ManagementService;

@Slf4j
@RequiredArgsConstructor
public abstract class Auth0Service {

    public abstract void saveUserInAuth0(String username, String password,
        String guestHistoryNumber) throws Auth0ApiException;

    protected abstract ManagementService getAuthManagementService();

    protected void createUserInAuth0(Auth0User newUser) {
        getAuthManagementService().createUser(newUser);
    }

    protected void updateUserInAuth0(String userId, Auth0User user) {
        try {
            getAuthManagementService().updateUser(userId, user);
        } catch (Auth0ApiException e) {
            log.error("Auth0ApiException", e);
        }
    }
}
