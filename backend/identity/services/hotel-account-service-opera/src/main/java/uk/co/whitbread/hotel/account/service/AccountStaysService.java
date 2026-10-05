package uk.co.whitbread.hotel.account.service;

import static uk.co.whitbread.hotel.account.utils.Utils.sanitizeInputString;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.co.whitbread.hotel.account.exceptions.InvalidTokenException;
import uk.co.whitbread.hotel.account.model.BBStaysRequest;
import uk.co.whitbread.hotel.account.model.StaysResponse;
import uk.co.whitbread.hotel.account.service.cdh.CdhBbBookingsService;
import uk.co.whitbread.hotel.account.service.cdh.CdhPiBookingsService;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;

@Slf4j
@Service
@AllArgsConstructor
public class AccountStaysService {

  private static final String INVALID_TOKEN_ERROR = "Required information not found in token";
  private final TokenService authTokenService;
  private final CdhBbBookingsService cdhBbBookingsService;
  private final CdhPiBookingsService cdhPiBookingsService;

  public StaysResponse getAccountStays(String customerId, String authorization,
      BBStaysRequest bbStaysRequest, Integer pageIndex, Integer pageSize) {

    boolean business = bbStaysRequest.isBusiness();
    log.info("Called getAccountStays with customerId: {} ; business: {}",
        sanitizeInputString(customerId), business);

    if (business) {
      return retrieveBbBookings(authorization, bbStaysRequest, pageIndex, pageSize);
    }
    return retrievePiBookings(customerId, authorization, bbStaysRequest, pageIndex, pageSize);
  }

  private StaysResponse retrievePiBookings(String customerId, String authorization,
      BBStaysRequest bbStaysRequest, Integer pageIndex, Integer pageSize) {

    String customerAccountId = authTokenService.retrieveCustomerAccountIdAndVerifyToken(
        authorization).orElse(null);
    if (StringUtils.isBlank(customerAccountId)) {
      throw new InvalidTokenException(INVALID_TOKEN_ERROR);
    }
    return cdhPiBookingsService.retrieveCdhPiBookingsV2(bbStaysRequest, customerAccountId,
        customerId, pageIndex, pageSize);
  }

  private StaysResponse retrieveBbBookings(String authorization, BBStaysRequest bbStaysRequest,
      Integer pageIndex, Integer pageSize) {

    CdhEmployeeDetails cdhEmployeeDetails =
        authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorization);
    if (StringUtils.isBlank(cdhEmployeeDetails.getEmployeeAccountId())
        || StringUtils.isBlank(cdhEmployeeDetails.getCompanyAccountId())) {
      throw new InvalidTokenException(INVALID_TOKEN_ERROR);
    }
    return cdhBbBookingsService.retrieveCdhBbBookingsV2(bbStaysRequest, cdhEmployeeDetails,
        pageIndex, pageSize);
  }

}
