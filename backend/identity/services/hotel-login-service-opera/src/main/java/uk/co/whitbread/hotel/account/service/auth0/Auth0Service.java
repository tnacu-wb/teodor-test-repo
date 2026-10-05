package uk.co.whitbread.hotel.account.service.auth0;

import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.users.User;
import com.auth0.net.Request;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.hotel.account.properties.Auth0Properties;
import uk.co.whitbread.hotel.account.utils.auth.Auth0ManagementTransformer;
import uk.co.whitbread.shared.auth.exception.AuthServiceException;
import uk.co.whitbread.shared.auth.exception.InvalidLoginException;
import uk.co.whitbread.shared.auth.service.EncryptionService;
import uk.co.whitbread.shared.auth.service.ManagementService;

@Slf4j
@RequiredArgsConstructor
public abstract class Auth0Service {

    private final Auth0Properties auth0Properties;
    private final Auth0ManagementTransformer auth0ManagementTransformer;
    private final EncryptionService encryptionService;

    protected abstract ManagementService getAuthManagementService();

    protected abstract Map<String, Object> buildUserAppMetadata(String encryptedGuestHistoryNumber);

    public void saveUserInAuth0(String username, String password, String guestHistoryNumber) {
        if (!auth0Properties.isLazyMigrationEnabled()) {
            return;
        }
        log.debug("Checking user not already in Auth0");

        String encryptedGuestHistoryNumber;
        try {
            getAuthManagementService().checkUserNotAlreadyInAuth0(username);
            encryptedGuestHistoryNumber = encryptionService.writeSecuredMessage(guestHistoryNumber);
        } catch (AuthServiceException e) {
            log.debug("User already exists in Auth0");
            return;
        } catch (InvalidLoginException e) {
            log.error("Guest history number encryption failed");
            return;
        }

        User auth0User = auth0ManagementTransformer.transform(username, password,
            buildUserAppMetadata(encryptedGuestHistoryNumber));
        auth0User.setConnection(getAuthManagementService().getManagementProperties().getConnection());
        log.debug("Creating user in Auth0");
        createUserInAuth0(auth0User);
    }

    public void createUserInAuth0(User newUser) {
        try {
            Request<User> userRequest = getAuthManagementService().retrieveManagementAPI().users()
                .create(newUser);
            userRequest.execute();
        } catch (Auth0Exception e) {
            log.error("Auth0Exception", e);
        }
    }
}
