package uk.co.whitbread.hotel.account.service.auth0;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.co.whitbread.hotel.account.properties.Auth0Properties;
import uk.co.whitbread.shared.auth.service.ManagementService;
import uk.co.whitbread.shared.auth.service.TokenService;

@Slf4j
@Service
@Qualifier("auth0BusinessService")
public class Auth0BusinessService extends Auth0Service {

  private final ManagementService businessManagementService;

  public Auth0BusinessService(ManagementService businessManagementService,
                              Auth0Properties auth0Properties,
                              TokenService tokenService) {

    super(auth0Properties, tokenService);
    this.businessManagementService = businessManagementService;
  }

  @Override
  public ManagementService getAuthManagementService() {
    return businessManagementService;
  }

  @Override
  String getAuth0Connection() {
    return auth0Properties.getB2bConnection();
  }
}