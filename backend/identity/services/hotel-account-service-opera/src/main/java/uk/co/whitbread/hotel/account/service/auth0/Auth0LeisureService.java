package uk.co.whitbread.hotel.account.service.auth0;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.co.whitbread.hotel.account.properties.Auth0Properties;
import uk.co.whitbread.shared.auth.service.ManagementService;
import uk.co.whitbread.shared.auth.service.TokenService;

@Slf4j
@Service
@Qualifier("auth0LeisureService")
public class Auth0LeisureService extends Auth0Service {

  private final ManagementService leisureManagementService;

  public Auth0LeisureService(ManagementService leisureManagementService,
                             Auth0Properties auth0Properties,
                             TokenService tokenService) {

    super(auth0Properties, tokenService);
    this.leisureManagementService = leisureManagementService;
  }

  @Override
  public ManagementService getAuthManagementService() {
    return leisureManagementService;
  }

  @Override
  String getAuth0Connection() {
    return auth0Properties.getB2cConnection();
  }
}
