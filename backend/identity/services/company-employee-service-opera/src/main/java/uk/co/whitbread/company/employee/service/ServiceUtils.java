package uk.co.whitbread.company.employee.service;

import java.util.concurrent.CompletableFuture;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.company.employee.exceptions.CompanyNotFoundException;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.azureemail.model.RequestToJoinAccepted;
import uk.co.whitbread.shared.azureemail.model.RequestToJoinRejected;
import uk.co.whitbread.shared.azureemail.service.AzureEmailService;
import uk.co.whitbread.shared.cdh.CompanyDataService;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServiceUtils {

  public static void sendRequestToJoinRejectedEmail(CompanyDataService companyDataService,
        AzureEmailService emailService, GetEmployeeResponse employee,
        CdhEmployeeDetails travelMangerDetails, String language) {
    CompletableFuture.runAsync(() -> {
      var companyId = employee.getCompanyAccountId();
      var company = companyDataService
            .getCompany(companyId, travelMangerDetails.getUserEmail());
      if (company.isEmpty()) {
        String errorMessage = String.format("Company with id %s was not found", companyId);
        log.error(errorMessage);
        throw new CompanyNotFoundException(errorMessage);
      }
      var requestToJoinRejected = RequestToJoinRejected.builder()
            .companyName(company.get().getCompanyName())
            .firstName(employee.getFirstName())
            .lastName(employee.getLastName())
            .emailAddress(employee.getEmailAddress())
            .language(language)
            .build();
      emailService.sendRequestToJoinRejectedEmail(requestToJoinRejected);
    });
  }

  public static void sendAsyncRequestToJoinAccepted(CompanyDataService companyDataService,
        AzureEmailService emailService, GetEmployeeResponse employeeResponse,
        String language, String activationUrl) {
    CompletableFuture.runAsync(() -> {
      var optionalCompanyResponse = companyDataService.getCompany(
            employeeResponse.getCompanyAccountId(), employeeResponse.getEmailAddress());

      if (optionalCompanyResponse.isEmpty()) {
        throw new CompanyNotFoundException(
              "Company with id " + employeeResponse.getCompanyAccountId() + " was not found");
      }

      var employeeAcceptToJoinNotification = RequestToJoinAccepted.builder()
            .email(employeeResponse.getEmailAddress())
            .firstName(employeeResponse.getFirstName())
            .lastName(employeeResponse.getLastName())
            .bbActivatedVerificationUrl(activationUrl)
            .companyName(optionalCompanyResponse.get().getCompanyName())
            .language(language)
            .build();
      emailService.sendBBRequestToJoinAcceptedEmail(
            employeeAcceptToJoinNotification);
    });
  }
}
