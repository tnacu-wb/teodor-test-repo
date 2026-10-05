package uk.co.whitbread.hotel.account.service.auth0;

import static uk.co.whitbread.hotel.account.utils.auth.Auth0ManagementTransformer.GHN_LABEL;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.co.whitbread.hotel.account.properties.Auth0Properties;
import uk.co.whitbread.hotel.account.utils.auth.Auth0ManagementTransformer;
import uk.co.whitbread.shared.auth.service.EncryptionService;
import uk.co.whitbread.shared.auth.service.ManagementService;

@Slf4j
@Service
@Qualifier("auth0LeisureService")
public class Auth0LeisureService extends Auth0Service {

    private final ManagementService leisureManagementService;

    public Auth0LeisureService(ManagementService leisureManagementService,
        Auth0Properties auth0Properties, Auth0ManagementTransformer auth0ManagementTransformer,
        EncryptionService encryptionService) {

        super(auth0Properties, auth0ManagementTransformer, encryptionService);
        this.leisureManagementService = leisureManagementService;
    }

    @Override
    protected ManagementService getAuthManagementService() {
        return leisureManagementService;
    }

    @Override
    protected Map<String, Object> buildUserAppMetadata(String encryptedGuestHistoryNumber) {
        return Map.of(GHN_LABEL, encryptedGuestHistoryNumber);
    }
}