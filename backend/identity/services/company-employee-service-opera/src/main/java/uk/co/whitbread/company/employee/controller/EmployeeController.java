package uk.co.whitbread.company.employee.controller;

import static uk.co.whitbread.company.employee.utils.Auth0ManagementTransformer.ACTIVATION_KEY_LABEL;
import static uk.co.whitbread.company.employee.utils.Auth0ManagementTransformer.EMPLOYEE_STATUS_LABEL;
import static uk.co.whitbread.company.employee.utils.SanitizingUtils.sanitize;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.co.whitbread.company.employee.exceptions.EmployeeNotFoundException;
import uk.co.whitbread.company.employee.exceptions.InvalidTokenException;
import uk.co.whitbread.company.employee.mapper.EmployeeMapper;
import uk.co.whitbread.company.employee.model.AccessLevel;
import uk.co.whitbread.company.employee.model.Employee;
import uk.co.whitbread.company.employee.model.EmployeeStatus;
import uk.co.whitbread.company.employee.model.GetEmployeeActivationResponse;
import uk.co.whitbread.company.employee.model.GetEmployeesRequest;
import uk.co.whitbread.company.employee.model.GetEmployeesResponse;
import uk.co.whitbread.company.employee.model.InviteRequest;
import uk.co.whitbread.company.employee.model.UpdateAccessLevelRequest;
import uk.co.whitbread.company.employee.service.Auth0Service;
import uk.co.whitbread.company.employee.service.EmployeeActivationService;
import uk.co.whitbread.company.employee.service.EmployeeService;
import uk.co.whitbread.company.employee.service.MarketingService;
import uk.co.whitbread.company.employee.utils.BookingChannel;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/companies")
@RestController
public class EmployeeController implements EmployeeApiDocumentation {

  private static final String SESSION_ID_HEADER = "session-id";
  private static final String COMPANY_ID_HEADER = "company-id";

  private final EmployeeService employeeService;
  private final EmployeeMapper employeeMapper;
  private final EmployeeActivationService employeeActivationService;
  private final Auth0Service auth0Service;
  private final TokenService authTokenService;
  private final MarketingService marketingService;

  @Override
  @RequestMapping(value = "/{companyId}/employees/{employeeId}", method = RequestMethod.PUT,
      consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ResponseEntity<Void> updateEmployee(
      @RequestHeader(value = SESSION_ID_HEADER, required = false) String sessionId,
      @RequestHeader(name = "Authorization", required = false) String authorization,
      @RequestHeader(name = "country", required = false) String countryCode,
      @RequestHeader(name = "language", required = false) String languageCode,
      @PathVariable("companyId") String companyId,
      @PathVariable("employeeId") String employeeId,
      @RequestHeader(name = "bookingChannel", required = false, defaultValue = "CBT") BookingChannel bookingChannel,
      @RequestParam(required = false, name = "activation-key") String activationKey,
      @Valid @RequestBody Employee employee) {
    log.info("Called PUT /companies/{}/employees/{} with sessionId: {}", sanitize(companyId),
        sanitize(employeeId), sanitize(sessionId));

    if (StringUtils.isNotEmpty(activationKey)) {
      employeeService.activateCdhEmployee(activationKey, employee, employeeId);
    } else {
      CdhEmployeeDetails cdhEmployeeDetails =
          authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorization);
      employeeService
          .updateCdhEmployee(companyId, employeeId, employee, cdhEmployeeDetails, languageCode);
    }
    marketingService.updateMarketingOptIn(employee.getUpdatePreferencesRequest(),
        employee.getEmailAddress());

    return ResponseEntity.noContent().build();
  }

  @Override
  @RequestMapping(value = "/admin/{companyId}/employees/{employeeId}/accesslevel", method = RequestMethod.PATCH,
      consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ResponseEntity<Void> updateEmployeeAccessLevel(
      @RequestHeader(name = SESSION_ID_HEADER, required = false) String sessionId,
      @RequestHeader(name = "Authorization", required = false) String authorization,
      @RequestHeader(name = "country", required = false) String countryCode,
      @RequestHeader(name = "language", required = false) String languageCode,
      @PathVariable("companyId") String companyId,
      @PathVariable("employeeId") String employeeId,
      @RequestHeader(name = "bookingChannel", required = false, defaultValue = "CBT") BookingChannel bookingChannel,
      @Valid @RequestBody UpdateAccessLevelRequest request) {

    log.info("Called PATCH companies/admin/{}/employees/{}/accesslevel with sessionId: {}",
        sanitize(companyId), sanitize(employeeId), sanitize(sessionId));

    final CdhEmployeeDetails cdhEmployeeDetails =
        authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorization);
    String travelManagerEmail = cdhEmployeeDetails.getUserEmail();
    employeeService
        .updateCdhEmployeeAccessLevel(companyId, employeeId, request.getAccessLevel(),
            travelManagerEmail, languageCode);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Override
  @RequestMapping(value = "/{companyId}/employees", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public GetEmployeesResponse getEmployees(
      @RequestHeader(name = SESSION_ID_HEADER, required = false) String sessionId,
      @RequestHeader(name = "Authorization", required = false) String authorization,
      @PathVariable("companyId") String companyId,
      @RequestHeader(name = "bookingChannel", required = false, defaultValue = "CBT") BookingChannel bookingChannel,
      @Parameter(hidden = true) @Validated @ModelAttribute GetEmployeesRequest findEmployeesRequest) {

    log.info("Called GET /companies/{}/employees", sanitize(companyId));
    if (StringUtils.isBlank(authorization)) {
      throw new InvalidTokenException("Missing authorization token.");
    }
    var cdhEmployeeDetails = authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(
        authorization);
    var userEmail = cdhEmployeeDetails.getUserEmail();
    if (canViewCompanyEmployees(cdhEmployeeDetails, companyId)) {
      return employeeService.getEmployeesFromCdh(findEmployeesRequest, companyId, userEmail);
    }
    throw new InvalidTokenException(
        "You do not have permission to view employees for this company.");
  }

  @Override
  @RequestMapping(value = "/{companyId}/employees/{employeeId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Employee> getEmployee(
      @RequestHeader(value = SESSION_ID_HEADER, required = false) String sessionId,
      @RequestHeader(name = "Authorization", required = false) String authorization,
      @PathVariable("companyId") String companyId,
      @PathVariable("employeeId") String employeeId,
      @RequestHeader(name = "bookingChannel", required = false, defaultValue = "CBT") BookingChannel bookingChannel,
      @RequestParam(required = false, name = "activation-key") String activationKey) {

    final HttpHeaders headers = new HttpHeaders();


    log.info("Called GET /companies/{}/employees/{}", sanitize(companyId),sanitize(employeeId));
    final CdhEmployeeDetails cdhEmployeeDetails =
        authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorization);
    final String userEmail = cdhEmployeeDetails.getUserEmail();
    final Employee employee = getEmployeeFromCdh(companyId, employeeId, activationKey,
        userEmail);
    return ResponseEntity.ok().headers(headers).body(employee);
  }

  @Override
  @RequestMapping(value = "/admin/{companyId}/employees", method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<Void> addEmployee(
      @RequestHeader(value = SESSION_ID_HEADER, required = false) String sessionId,
      @PathVariable("companyId") String companyId,
      @RequestHeader(name = "country", required = false) String countryCode,
      @RequestHeader(name = "language", required = false) String languageCode,
      @RequestHeader(name = "bookingChannel", required = false, defaultValue = "CBT") BookingChannel bookingChannel,
      @RequestHeader(name = "Authorization", required = false) String authorization,
      @Valid @RequestBody Employee employee) {

    log.info("Called POST /companies/admin/{}/employees with sessionId: {}", sanitize(companyId),
        sanitize(sessionId));

    final CdhEmployeeDetails cdhEmployeeDetails =
        authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorization);
    var employeeId = employeeService.addCdhEmployee(companyId, employee, cdhEmployeeDetails, languageCode);


    URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
        .buildAndExpand(employeeId).toUri();
    return ResponseEntity.created(location).build();
  }

  @Override
  @RequestMapping(value = "/admin/{companyId}/employees/invite", method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<Void> inviteEmployee(
      @RequestHeader(value = SESSION_ID_HEADER, required = false) String sessionId,
      @RequestHeader(value = "Authorization", required = false) String authorization,
      @PathVariable("companyId") String companyId,
      @RequestHeader(name = "country", required = false) String countryCode,
      @RequestHeader(name = "language", required = false) String languageCode,
      @RequestHeader(name = "bookingChannel", required = false, defaultValue = "CBT") BookingChannel bookingChannel,
      @Valid @RequestBody InviteRequest inviteRequest) {

    log.info("Called POST /companies/admin/{}/employees/invite with sessionId: {}", sanitize(companyId),
        sanitize(sessionId));

    final CdhEmployeeDetails cdhEmployeeDetails =
        authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorization);
    employeeService.inviteCdhEmployee(companyId, inviteRequest, cdhEmployeeDetails, languageCode);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @Override
  @RequestMapping(value = "/employees/activation-details", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Employee> getActivationDetails(
      @RequestHeader(name = "bookingChannel", required = false, defaultValue = "CBT") BookingChannel bookingChannel,
      @RequestHeader(name = "country", required = false, defaultValue = "gb") String countryCode,
      @RequestHeader(name = "language", required = false, defaultValue = "en") String languageCode,
      @RequestParam(name = "activation-key") String activationKey) {

    log.info("Called GET /companies/employees/activation-details with activation-key: {}",
        sanitize(activationKey));

    GetEmployeeActivationResponse response = employeeActivationService
        .getEmployeeActivationDetails(activationKey);
    HttpHeaders headers = prepareEmployeeActivationResponseHeaders(response);
    return ResponseEntity.ok().headers(headers).body(response.getEmployee());
  }

  @Override
  @RequestMapping(value = "/activation", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Employee> activationTravelManagerAccount(
      @RequestHeader(name = "bookingChannel", required = false, defaultValue = "CBT") BookingChannel bookingChannel,
      @RequestHeader(name = "country", required = false, defaultValue = "gb") String countryCode,
      @RequestHeader(name = "language", required = false, defaultValue = "en") String languageCode,
      @RequestParam(name = "activation-key") String activationKey) {

    log.info("Called POST /companies/activation with activation-key: {}", sanitize(activationKey));

    Employee employee = employeeActivationService.activateTravelManagerInCdh(activationKey);
    Map<String, Object> appMetadata = new HashMap<>(
        Map.of(EMPLOYEE_STATUS_LABEL, EmployeeStatus.ACTIVE));
    appMetadata.put(ACTIVATION_KEY_LABEL, null);
    auth0Service.updateAppMetadata(employee.getEmailAddress(), appMetadata);
    return ResponseEntity.ok().body(employee);
  }

  private Employee getEmployeeFromCdh(String companyId, String employeeId, String activationKey,
      String userEmail) {
    if (StringUtils.isEmpty(userEmail)) {
      throw new EmployeeNotFoundException(String.format(
          "User email address could not be extracted from token for employee with company ID %s and employee ID %s",
          companyId, employeeId));
    }
    if (activationKey != null && !activationKey.isEmpty()) {
      return employeeService.getCdhEmployee(activationKey, userEmail);
    }
    return employeeMapper.toEmployee(employeeService.getCdhEmployee(companyId, employeeId, userEmail));
  }

  private HttpHeaders prepareEmployeeActivationResponseHeaders(
      GetEmployeeActivationResponse response) {
    HttpHeaders headers = new HttpHeaders();
    String exposedHeaders = Strings.EMPTY;

    if (Objects.nonNull(response.getSessionId())) {
      headers.add(SESSION_ID_HEADER, response.getSessionId());
      exposedHeaders = SESSION_ID_HEADER;
    }

    if (Objects.nonNull(response.getCompanyId())) {
      headers.add(COMPANY_ID_HEADER, response.getCompanyId());
      if (!(exposedHeaders.isEmpty())) {
        exposedHeaders = exposedHeaders + ", ";
      }
      exposedHeaders = exposedHeaders + COMPANY_ID_HEADER;
    }

    if (!exposedHeaders.isEmpty()) {
      headers.add("Access-Control-Expose-Headers", exposedHeaders);
    }

    return headers;
  }

  private static boolean canViewCompanyEmployees(CdhEmployeeDetails employee, String companyId) {
    var accessLevel = employee.getAccessLevel();
    return (AccessLevel.SUPER.name().equalsIgnoreCase(accessLevel) ||
        AccessLevel.BOOKER.name().equalsIgnoreCase(accessLevel) ||
        AccessLevel.BUSINESS_PAY_MANAGER.name().equalsIgnoreCase(accessLevel)) &&
        companyId.equals(employee.getCompanyAccountId());
  }
}
