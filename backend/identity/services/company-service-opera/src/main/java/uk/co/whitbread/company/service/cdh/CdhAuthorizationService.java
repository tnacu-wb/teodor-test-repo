package uk.co.whitbread.company.service.cdh;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.company.client.model.AccessLevel;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;

@Slf4j
@Service
@RequiredArgsConstructor
public class CdhAuthorizationService {


  public boolean isSuperAccessLevelUser(CdhEmployeeDetails tokenDetails) {
    return Optional.of(tokenDetails)
        .map(accessLevel -> AccessLevel.SUPER.name().equals(accessLevel.getAccessLevel().toUpperCase()))
        .orElse(false);
  }

  public boolean isBusinessPayManagerLevelUser(CdhEmployeeDetails tokenDetails) {
    return Optional.of(tokenDetails)
        .map(accessLevel -> AccessLevel.BUSINESS_PAY_MANAGER.name().equals(accessLevel.getAccessLevel().toUpperCase()))
        .orElse(false);
  }

  public boolean isSameCompany(String companyAccountIdFromToken, String companyIdFromPath) {
    return Optional.ofNullable(companyAccountIdFromToken)
        .map(actualCompanyId -> actualCompanyId.equals(companyIdFromPath))
        .orElse(false);
  }

}
