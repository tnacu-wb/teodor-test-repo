package uk.co.whitbread.employee.bulk.service;

import com.opencsv.CSVWriter;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.co.whitbread.employee.bulk.ErrorCode;
import uk.co.whitbread.employee.bulk.client.CdhBulkEmployeeClient;
import uk.co.whitbread.employee.bulk.converter.CdhBulkEmployeeConverter;
import uk.co.whitbread.employee.bulk.exception.BulkEmployeeUploadException;
import uk.co.whitbread.employee.bulk.exception.BulkEmployeeUploadFileConversionException;
import uk.co.whitbread.employee.bulk.exception.CompanyDoesntMatchException;
import uk.co.whitbread.employee.bulk.exception.CompanyNotFoundException;
import uk.co.whitbread.employee.bulk.exception.GetEmployeeCSVListException;
import uk.co.whitbread.employee.bulk.exception.ValidationError;
import uk.co.whitbread.employee.bulk.mapper.EmployeeMapper;
import uk.co.whitbread.employee.bulk.model.Employee;
import uk.co.whitbread.employee.bulk.model.EmployeeWithRowNumber;
import uk.co.whitbread.employee.bulk.model.HeaderProperties;
import uk.co.whitbread.employee.bulk.model.LanguageCode;
import uk.co.whitbread.employee.bulk.model.companyEmployees.GetEmployeeResponse;
import uk.co.whitbread.employee.bulk.model.companyEmployees.GetEmployeesResponse;
import uk.co.whitbread.employee.bulk.properties.EmailProperties;
import uk.co.whitbread.employee.bulk.utils.EmployeeFileWriter;
import uk.co.whitbread.hotel.cdh.adapter.generated.models.GetEmployeesResponseDto;
import uk.co.whitbread.shared.auth.exception.TokenVerificationException;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;
import uk.co.whitbread.shared.azureemail.model.EmployeeInvite;
import uk.co.whitbread.shared.azureemail.service.AzureEmailService;
import uk.co.whitbread.shared.cdh.CompanyDataService;
import uk.co.whitbread.shared.cdh.EmployeeDataService;
import uk.co.whitbread.shared.cdh.exception.CDHException;
import uk.co.whitbread.shared.cdh.model.EmployeeAccountRequest;
import uk.co.whitbread.shared.cdh.model.EmployeeAccountResponse;
import uk.co.whitbread.shared.cdh.model.company.GetCompanyResponse;

@Service
@Slf4j
@AllArgsConstructor
public class CdhBulkEmployeeService {

  protected static final Integer MAX_PAGE_SIZE = 300;
  public static final String SUPER_ACCESS_LEVEL = "SUPER";

  private final EmployeeDataService employeeDataService;
  private final CompanyDataService companyDataService;
  private final CdhBulkEmployeeConverter employeeConverter;
  private final EmployeeFileWriter employeeFileWriter;
  private final AzureEmailService emailService;
  private final EmailProperties emailProperties;
  private final CdhBulkEmployeeClient cdhBulkEmployeeClient;
  private final EmployeeMapper employeeMapper;
  
  private final TokenService authTokenService;

  public boolean bulkAddEmployees(String authorization, String companyId, MultipartFile upFile, String language,
                                  boolean innBusiness) {
    log.debug("Called CdhBulkEmployeeService.bulkAddEmployees");

    var accessedBy = validateAccessAndGetUserEmail(authorization, companyId);

    try {
      final List<ValidationError> failedEmployees = new LinkedList<>();
      AtomicBoolean isEmployeeCreated = new AtomicBoolean(false);
      final List<EmployeeWithRowNumber> employeeList = employeeConverter
          .convertEmployeeSpreadsheetToEmployeeAccountRequest(upFile, failedEmployees);
      if (employeeList.isEmpty()) {
        return false;
      }
      employeeList.forEach(
          employeeWithRow -> {
            if (!hasValidationError(employeeWithRow.getRowNumber(), failedEmployees)) {
              var employeeAccount = processEmployee(employeeWithRow.getEmployee(), 
                  employeeWithRow.getRowNumber(), companyId, failedEmployees, accessedBy);
              if (employeeAccount != null && Objects.nonNull(employeeAccount.getEmployeeAccountId())) {
                isEmployeeCreated.set(true);
              }
              Optional.ofNullable(employeeAccount)
                  .ifPresent(emp -> sendAsyncBBInviteEmployeeEmail(employeeWithRow.getEmployee(), 
                      emp.getActivationKey(), language, innBusiness));
            }
          });
      if (!failedEmployees.isEmpty()) {
        // we indicate it to FE to know to display a specific error message
        if (isEmployeeCreated.get()) {
          failedEmployees.add(
              new ValidationError(
                  String.valueOf(ErrorCode.FILE_PARTIAL_PROCESSED.getCode()), ""));
        }
        throw new BulkEmployeeUploadException(failedEmployees);
      }
      return true;
    } catch (IOException e) {
      throw new BulkEmployeeUploadFileConversionException(
          "Failed to convert MultipartFile to File: " + e);
    }
  }

  private String validateAccessAndGetUserEmail(String authorization, String companyId) {

    if (StringUtils.isEmpty(authorization)) {
      throw new TokenVerificationException("Authorization token is missing.");
    }
    var cdhEmployeeDetails = authTokenService
        .retrieveCdhEmployeeDetailsAndVerifyToken(authorization);
    if (Boolean.FALSE.equals(isValidCompanyId(cdhEmployeeDetails, companyId))) {
      throw new CompanyDoesntMatchException(
          "Company id from request doesn't match the one from jwt token");
    }
    var userEmail = cdhEmployeeDetails.getUserEmail();
    var accessLevel = cdhEmployeeDetails.getAccessLevel();
    if (!SUPER_ACCESS_LEVEL.equalsIgnoreCase(accessLevel)) {
          throw new TokenVerificationException("Invalid access level.");
    }
    return userEmail;
  }

  private Boolean isValidCompanyId(CdhEmployeeDetails cdhEmployeeDetails, String companyId) {
    return cdhEmployeeDetails.getCompanyAccountId().equals(companyId);
  }

  private void sendAsyncBBInviteEmployeeEmail(EmployeeAccountRequest employee, String activationKey, String language,
                                              boolean innBusiness) {
    CompletableFuture.runAsync(() -> {
      String activationLink = getActivationLink(language, activationKey, innBusiness);
      var employeeInvite = EmployeeInvite.builder()
          .email(employee.getEmailAddress())
          .accessLevel(employee.getAccessLevel())
          .activationLink(activationLink)
          .language(language)
          .build();
      emailService.sendBBInviteEmployeeEmail(employeeInvite);
    });
  }

  private String getActivationLink(String language, String activationKey, boolean innBusiness) {
    if (innBusiness) {
      return getInnBusinessActivationLink(language, activationKey);
    }
    if (LanguageCode.DE.asLowerCase().equalsIgnoreCase(language)) {
      return emailProperties.getEmployeeActivationUrlDe() + activationKey;
    }
    return emailProperties.getEmployeeActivationUrl() + activationKey;
  }

  private String getInnBusinessActivationLink(String language, String activationKey) {
    if (LanguageCode.DE.asLowerCase().equalsIgnoreCase(language)) {
      return emailProperties.getInnBusinessEmployeeActivationUrlDe() + activationKey;
    }
    return emailProperties.getInnBusinessEmployeeActivationUrl() + activationKey;
  }

  public ByteArrayOutputStream bulkGetEmployees(String authorization, String companyId) {
    log.debug("Called BulkEmployeeService.bulkGetEmployees");

    var accessedBy = validateAccessAndGetUserEmail(authorization, companyId);

    ByteArrayOutputStream employeeListOutputStream = new ByteArrayOutputStream();
    CSVWriter csvWriter = new CSVWriter(
        new BufferedWriter(new OutputStreamWriter(employeeListOutputStream)));
    List<Employee> employeeList = getEmployees(companyId, accessedBy);
    HeaderProperties headerProperties = getHeaderProperties(companyId, accessedBy);
    try {
      employeeFileWriter.populateCsv(headerProperties, csvWriter, employeeList);
    } catch (IOException ioe) {
      throw new GetEmployeeCSVListException("Failed to write to CSV file:" + ioe);
    }
    return employeeListOutputStream;
  }

  private List<Employee> getEmployees(String companyId, String accessedBy) {
    AtomicBoolean continuationTokenIsPresent = new AtomicBoolean(true);
    AtomicReference<String> pageToken = new AtomicReference<>(null);
    List<GetEmployeeResponse> allEmployees = new ArrayList<>();
    do {
      try {
        GetEmployeesResponseDto getEmployeesResponseDto =
            cdhBulkEmployeeClient.getCompanyEmployees(companyId, accessedBy, MAX_PAGE_SIZE,
                pageToken.get(), "PI", false);
        
        Optional<GetEmployeesResponse> opGetEmployeesResponse =
            Optional.ofNullable(employeeMapper.toModel(getEmployeesResponseDto));
        opGetEmployeesResponse.ifPresentOrElse(getEmployeesResponse -> {
          List<GetEmployeeResponse> employees = getEmployeesResponse.getResults();
          if (Objects.isNull(employees) || employees.isEmpty()) {
            continuationTokenIsPresent.set(false);
          } else {
            allEmployees.addAll(employees);
            if (StringUtils.isNotBlank(getEmployeesResponse.getContinuationToken())) {
              pageToken.set(getEmployeesResponse.getContinuationToken());
            } else {
              continuationTokenIsPresent.set(false);
            }
          }
        }, () -> continuationTokenIsPresent.set(false));
      } catch (CDHException e) {
        if (!(e.getStatus() == HttpStatus.SC_NOT_FOUND)) {
          throw e;
        }
        continuationTokenIsPresent.set(false);
      }
    } while (continuationTokenIsPresent.get());

    if (!allEmployees.isEmpty()) {
      return employeeFileWriter.convertToEmployeeList(allEmployees);
    } else {
      return Collections.emptyList();
    }
  }

  private HeaderProperties getHeaderProperties(String companyId, String accessedBy) {
    final GetCompanyResponse company = companyDataService.getCompany(companyId, accessedBy)
        .orElseThrow(
            () -> new CompanyNotFoundException("Could not find company with id " + companyId));
    return employeeFileWriter.getCsvHeaderFieldsForCdh(company);
  }

  private EmployeeAccountResponse processEmployee(EmployeeAccountRequest employee, int entryIndex, String companyId,
      List<ValidationError> failedEmployees, String accessedBy) {
    EmployeeAccountResponse response = null;
    try {
      response = employeeDataService.createEmployeeAccount(companyId, employee, accessedBy);
    } catch (CDHException e) {
      handleFailedEmployee(entryIndex, e, failedEmployees);
    }
    return response;
  }

  private void handleFailedEmployee(int entryIndex, CDHException e, List<ValidationError> failedEmployees) {
    log.error("Error processing employee entry on row {}", entryIndex, e);
    var errorMessage = extractFirstErrorMessage(e.getMessage());
    failedEmployees.add(new ValidationError(errorMapping(errorMessage), String.valueOf(entryIndex)));
  }

  private String extractFirstErrorMessage(String message) {
    if (Strings.isNotBlank(message)) {
      Pattern errorMessagePattern = Pattern.compile("\"ErrorMessage\"\\s*:\\s*\"([^\"]+)\"");
      Matcher errorMessageMatcher = errorMessagePattern.matcher(message);
      String errorMessage = errorMessageMatcher.results()
          .map(mr -> mr.group(1))
          .findFirst()
          .orElse(null);
      if (errorMessage != null) {
        return errorMessage;
      }
      Pattern messagePattern = Pattern.compile("\"message\"\\s*:\\s*\"([^\"]+)\"");
      Matcher messageMatcher = messagePattern.matcher(message);
      return messageMatcher.results()
          .map(mr -> mr.group(1))
          .findFirst()
          .orElse(message);
    }
    return message;
  }

  private String errorMapping(String errorMessage) {
    if (Strings.isNotBlank(errorMessage)) {
      return switch (errorMessage) {
        case "Cannot create employee as it already exists" -> String.valueOf(ErrorCode.EMPLOYEE_EXISTS.getCode());
        case "EmailAddress field is mandatory" -> String.valueOf(ErrorCode.EMAIL_IS_EMPTY.getCode());
        case "EmailAddress field is invalid" -> String.valueOf(ErrorCode.EMAIL_IS_INVALID.getCode());
        default -> errorMessage;
      };

    }
    return "Unknown error";
  }

  private boolean hasValidationError(int rowNumber, List<ValidationError> failedEmployees) {
    return failedEmployees.stream()
        .anyMatch(error -> error.getErrorDescription().equals(String.valueOf(rowNumber)));
  }
}
