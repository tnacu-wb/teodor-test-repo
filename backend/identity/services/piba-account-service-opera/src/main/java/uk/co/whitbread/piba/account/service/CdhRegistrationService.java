package uk.co.whitbread.piba.account.service;

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.piba.account.exception.InValidTokenException;
import uk.co.whitbread.piba.account.exception.RegisterTetheredUserException;
import uk.co.whitbread.piba.account.mapper.PibaTetheredGuidResponseMapper;
import uk.co.whitbread.piba.account.mapper.TetheredMapper;
import uk.co.whitbread.piba.account.model.PibaTetheredGuidResponse;
import uk.co.whitbread.piba.account.model.TetheredUserRequest;
import uk.co.whitbread.piba.account.util.LogUtils;
import uk.co.whitbread.piba.account.validation.PibaGuidValidator;
import uk.co.whitbread.shared.auth.service.TokenService;
import uk.co.whitbread.shared.cdh.EmployeeDataService;
import uk.co.whitbread.shared.cdh.RegistrationDataService;
import uk.co.whitbread.shared.cdh.exception.CDHException;
import uk.co.whitbread.shared.cdh.model.GetDashboardDetailsQueryParams;

@Service
@Slf4j
@AllArgsConstructor
public class CdhRegistrationService {

  private final RegistrationDataService registrationDataService;
  private final TokenService tokenService;
  private final PibaTetheredGuidResponseMapper pibaTetheredGuidResponseMapper;
  private final TetheredMapper tetheredMapper;
  private final PibaGuidValidator pibaGuidValidator;
  private final EmployeeDataService employeeDataService;

  private static final String ACCESS_CONTEXT = "InnBusiness";

  public List<PibaTetheredGuidResponse> getTetheredGuids(String companyId, String employeeId, String accessedBy) {
    log.info("Retrieve tethered guids from CDH request for company id {}, employee id {}", companyId, employeeId);
    var pibaTetheredGuidResponseList = registrationDataService.getDashboardDetails(
        GetDashboardDetailsQueryParams.builder()
            .companyId(companyId)
            .employeeId(employeeId)
            .build(),
        accessedBy,
        ACCESS_CONTEXT);

    var response = pibaTetheredGuidResponseMapper.map(pibaTetheredGuidResponseList);

    pibaGuidValidator.validateMultiple(response);

    return response;
  }

  /**
   * Register tethered user.
   *
   * @param authorization       the authorization token
   * @param tetheredUserRequest the tethered user request
   */
  public void registerTetheredUser(String authorization, TetheredUserRequest tetheredUserRequest) {
    log.info("Register tethered guid in CDH request for company id {}, tetheredGuids id {}",
        LogUtils.sanitisedStringWithMaxLengthLimit(tetheredUserRequest.getCompanyId(), 50),
        LogUtils.sanitisedStringWithMaxLengthLimit(tetheredUserRequest.getTetheredGuids().toString(), 150));

    var accessedBy = Optional.ofNullable(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorization).getUserEmail())
        .orElseThrow(InValidTokenException::new);

    try {
      employeeDataService.registerTetheredUser(
          tetheredMapper.toTetheredUserRequest(tetheredUserRequest),
          accessedBy, ACCESS_CONTEXT);
    } catch (CDHException e) {
      log.error("Error while registering tethered user in CDH error {} and request {}",
          e,
          LogUtils.sanitisedStringWithMaxLengthLimit(tetheredUserRequest.toString(), 150));
      throw new RegisterTetheredUserException();
    }
  }
}
