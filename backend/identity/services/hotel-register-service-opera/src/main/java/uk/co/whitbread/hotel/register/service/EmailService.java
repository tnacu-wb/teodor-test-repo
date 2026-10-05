package uk.co.whitbread.hotel.register.service;

import static uk.co.whitbread.hotel.register.utils.register.cdh.CompanyUtils.buildGetCompaniesQueryParams;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uk.co.whitbread.hotel.register.exceptions.CdhServiceException;
import uk.co.whitbread.hotel.register.model.CompanyType;
import uk.co.whitbread.hotel.register.model.ContactDetail;
import uk.co.whitbread.hotel.register.model.Customer;
import uk.co.whitbread.hotel.register.model.InnBRegistrationStepOneRequest;
import uk.co.whitbread.hotel.register.model.LanguageCode;
import uk.co.whitbread.hotel.register.model.feature.FeatureFlag;
import uk.co.whitbread.hotel.register.model.feature.UnleashWrapper;
import uk.co.whitbread.hotel.register.properties.EmailProperties;
import uk.co.whitbread.hotel.register.utils.register.CustomerTransformer;
import uk.co.whitbread.shared.azureemail.model.CompanyActivation;
import uk.co.whitbread.shared.azureemail.model.EmployeeRegistrationNotification;
import uk.co.whitbread.shared.azureemail.model.PiRegister;
import uk.co.whitbread.shared.azureemail.service.AzureEmailService;
import uk.co.whitbread.shared.cdh.AccessLevel;
import uk.co.whitbread.shared.cdh.CompanyDataService;
import uk.co.whitbread.shared.cdh.EmployeeDataService;
import uk.co.whitbread.shared.cdh.exception.CDHException;
import uk.co.whitbread.shared.cdh.model.GetCompanyEmployeesQueryParams;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;
import uk.co.whitbread.shared.cdh.model.GetEmployeesResponse;
import uk.co.whitbread.shared.cdh.model.company.GetCompaniesResponse;
import uk.co.whitbread.shared.cdh.model.company.GetCompanyResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

  private final AzureEmailService azureEmailService;
  private final CompanyDataService cdhCompanyService;
  private final EmployeeDataService cdhEmployeeService;
  private final CountriesService countriesService;
  private final CustomerTransformer customerTransformer;
  private final EmailProperties emailProperties;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;

  @Async
  public void sendAsyncPiRegisterEmail(Customer newCustomer, String languageCode) {
    final String countryCode = newCustomer.getContactDetail().getAddress().getCountryCode();
    final String countryLegend = countriesService.getCountryLegend(countryCode);
    azureEmailService.sendPiRegisterEmail(
        customerTransformer.toPiRegister(newCustomer, languageCode, countryLegend));
  }

  @Async
  public void sendAsyncBBCompanyActivationEmail(GetEmployeeResponse employee, String companyName,
      String language, CompanyType companyType) {
    String activationLink;
    if (CompanyType.INNB.equals(companyType)) {
      activationLink = getInnbActivationLink(language, employee.getActivationKey());
    } else {
      activationLink = getActivationLink(language, employee.getActivationKey());
    }
    var companyActivation = CompanyActivation.builder()
        .email(employee.getEmailAddress())
        .firstName(employee.getFirstName())
        .lastName(employee.getLastName())
        .companyName(companyName)
        .activationLink(activationLink)
        .language(language)
        .build();
    try {
      azureEmailService.sendBBCompanyActivationEmail(companyActivation);
    } catch (Exception e) {
      log.error(String.format(
          "Sending of company activation email to travel manager failed; companyAccountId=%s , employeeAccountId=%s",
          employee.getCompanyAccountId(), employee.getEmployeeAccountId()), e);
    }
  }

  @Async
  public void sendAsyncRegisterNotificationEmails(Customer newCustomer, String language) {
    final ContactDetail contactDetail = newCustomer.getContactDetail();
    final String requesterFirstName = contactDetail.getFirstName();
    final String requesterLastName = contactDetail.getLastName();
    final String accessedBy = contactDetail.getEmail();
    final Optional<GetCompaniesResponse> companies = cdhCompanyService.getCompanies(
        buildGetCompaniesQueryParams(newCustomer), accessedBy);
    if (companies.isEmpty() || companies.get().getResults().isEmpty()) {
      throw new CdhServiceException(
          "The existing company could not be found for customer " + newCustomer);
    }
    final GetCompanyResponse company = companies.get().getResults().get(0);
    final List<GetEmployeeResponse> travelManagers = getApprovalManagers(company, accessedBy,
        AccessLevel.SUPER);
    travelManagers.forEach(travelManager -> {
      final String travelManagerEmailAddress = travelManager.getEmailAddress();
      try {
        azureEmailService.sendEmployeeRegistrationNotificationEmail(
            EmployeeRegistrationNotification.builder()
                .emailAddress(travelManagerEmailAddress)
                .firstName(travelManager.getFirstName())
                .lastName(travelManager.getLastName())
                .requesterFirstName(requesterFirstName)
                .requesterLastName(requesterLastName)
                .companyName(company.getCompanyName())
                .language(language)
                .build());
      } catch (Exception e) {
        log.error(String.format(
            "Sending of employee self-registration notification email to travel manager failed; companyAccountId=%s , TM employeeAccountId=%s",
            company.getCompanyAccountId(), travelManager.getEmployeeAccountId()), e);
      }
    });
  }

  @Async
  public void sendAsyncRegisterNotificationEmails(InnBRegistrationStepOneRequest request) {
    final Optional<GetCompaniesResponse> companies = cdhCompanyService.getCompanies(
        buildGetCompaniesQueryParams(request.getCompanyName(), request.getAddress()), request.getEmail());
    if (companies.isEmpty() || companies.get().getResults().isEmpty()) {
      throw new CdhServiceException(
          "The existing company could not be found for customer");
    }
    final GetCompanyResponse company = companies.get().getResults().get(0);
    final List<GetEmployeeResponse> approvalManagers;
    if (company.getCompanyType().equals(CompanyType.BP.getValue())) {
      approvalManagers = getApprovalManagers(company, request.getEmail(), AccessLevel.BUSINESS_PAY_MANAGER);
    } else {
      approvalManagers = getApprovalManagers(company, request.getEmail(), AccessLevel.SUPER);
    }

    approvalManagers.forEach(approvalManager -> {
      try {
        azureEmailService.sendEmployeeRegistrationNotificationEmail(
            EmployeeRegistrationNotification.builder()
                .emailAddress(approvalManager.getEmailAddress())
                .firstName(approvalManager.getFirstName())
                .lastName(approvalManager.getLastName())
                .requesterFirstName(request.getEmail())
                .requesterLastName("")
                .companyName(company.getCompanyName())
                .language(request.getLanguage())
                .build());
      } catch (Exception e) {
        log.error("Sending of employee self-registration notification email to approval manager failed.", e);
      }
    });
  }

  private String getActivationLink(String language, String activationKey) {
    if (LanguageCode.DE.asLowerCase().equalsIgnoreCase(language)) {
      return emailProperties.getCompanyActivationUrlDe() + activationKey;
    } else {
      return emailProperties.getCompanyActivationUrl() + activationKey;
    }
  }

  private String getInnbActivationLink(String language, String activationKey) {
    if (LanguageCode.DE.asLowerCase().equalsIgnoreCase(language)) {
      return emailProperties.getInnbCompanyActivationUrlDe() + activationKey;
    } else {
      return emailProperties.getInnbCompanyActivationUrl() + activationKey;
    }
  }

  private List<GetEmployeeResponse> getApprovalManagers(GetCompanyResponse company,
      String accessedBy, AccessLevel accessLevel) {
    var travelManagers = new ArrayList<GetEmployeeResponse>();
    var queryParams = GetCompanyEmployeesQueryParams.builder()
        .accessLevel(accessLevel)
        .pageSize(100)
        .build();
    try {
      do {
        var responseOptional = getCompanyEmployees(company.getCompanyAccountId(), queryParams,
            accessedBy);
        if (responseOptional.isPresent()) {
          var response = responseOptional.get();
          travelManagers.addAll(response.getResults());
          queryParams.setPageToken(response.getContinuationToken());
        }
      } while (Objects.nonNull(queryParams.getPageToken()));
    } catch (CDHException e) {
      log.error(String.format("Error while retrieving employees from company %s",
          company.getCompanyAccountId()), e);
      return travelManagers;
    }
    return travelManagers;
  }

  private Optional<GetEmployeesResponse> getCompanyEmployees(String companyAccountId,
      GetCompanyEmployeesQueryParams queryParams, String accessedBy) {
    if (isCdhApiDeprecationEnabled()) {
      return cdhEmployeeService.getCompanyEmployeesV2(companyAccountId, queryParams, accessedBy);
    }
    return cdhEmployeeService.getCompanyEmployees(companyAccountId, queryParams, accessedBy);
  }

  private boolean isCdhApiDeprecationEnabled() {
    return unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhApiDeprecation());
  }
}