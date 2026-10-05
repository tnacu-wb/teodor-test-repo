package uk.co.whitbread.hotel.register.service.auth0;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.co.whitbread.hotel.register.utils.register.Auth0ManagementTransformer;
import uk.co.whitbread.shared.auth.exception.Auth0ApiException;
import uk.co.whitbread.shared.auth.exception.AuthServiceException;
import uk.co.whitbread.shared.auth.model.Auth0User;
import uk.co.whitbread.shared.auth.service.ManagementService;

@Slf4j
@Service
@Qualifier("auth0LeisureService")
public class Auth0LeisureService extends Auth0Service {

    private final ManagementService leisureManagementService;
    private final Auth0ManagementTransformer auth0ManagementTransformer;

    public Auth0LeisureService(ManagementService leisureManagementService,
        Auth0ManagementTransformer auth0ManagementTransformer) {

        this.leisureManagementService = leisureManagementService;
        this.auth0ManagementTransformer = auth0ManagementTransformer;
    }

    @Override
    protected ManagementService getAuthManagementService() {
        return leisureManagementService;
    }

    @Override
    public void saveUserInAuth0(String username, String password, String guestHistoryNumber)
        throws Auth0ApiException {

        log.debug("Checking user not already in Auth0");

        try {
            leisureManagementService.checkUserNotAlreadyInAuth0(username);
        } catch (AuthServiceException _) {
            log.debug("User already exists in Auth0");
            return;
        }

        Auth0User auth0User = auth0ManagementTransformer.transform(username, password, Map.of());
        auth0User.setConnection(leisureManagementService.getManagementProperties().getConnection());
        log.debug("Creating user in Auth0");
        createUserInAuth0(auth0User);
    }
}
