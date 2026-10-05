package uk.co.whitbread.hotel.account.client.pibaAccount;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import uk.co.whitbread.hotel.account.exceptions.PibaAccountServiceException;
import uk.co.whitbread.shared.auth.service.TokenService;

@AllArgsConstructor
@Component
@Slf4j
public class PibaAccountClientFallbackFactory implements FallbackFactory<PibaAccountClient> {

  private final TokenService tokenService;

  @Override
  public PibaAccountClient create(Throwable throwable) {
      return (authorization, ignoreWorldlineDetails) -> {
        var cdhEmployeeDetails = tokenService
            .retrieveCdhEmployeeDetailsAndVerifyToken(authorization);
        throw new PibaAccountServiceException(
            String.format(
                "Error fetching tethered accounts from piba account service for companyAccountId=%s, employeeAccountId=%s",
                cdhEmployeeDetails.getCompanyAccountId(),
                cdhEmployeeDetails.getEmployeeAccountId()), throwable);
      };
  }
}
