package uk.co.whitbread.company.service.cdh;

import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.company.mapper.CompanyMapper;
import uk.co.whitbread.company.mapper.EmployeeMapper;
import uk.co.whitbread.company.model.Address;
import uk.co.whitbread.company.model.CheckCompanyResponse;
import uk.co.whitbread.company.model.CompanyDetailsResponse;
import uk.co.whitbread.company.model.CompanySummary;
import uk.co.whitbread.company.service.Auth0Service;
import uk.co.whitbread.company.utils.UpsellItemsAllowedUtils;
import uk.co.whitbread.shared.cdh.model.EmployeeAccountRequest;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;
import uk.co.whitbread.shared.cdh.model.company.CompanyAccountRequest;
import uk.co.whitbread.shared.cdh.model.company.GetCompaniesQueryParams;
import uk.co.whitbread.shared.cdh.model.company.GetCompanyResponse;
import static uk.co.whitbread.company.utils.Utils.sanitizeInputString;

@Slf4j
@Service
@RequiredArgsConstructor
public class CdhCompanyDetailsService {

  private static final String DUMMY_EMAIL = "dummy@mail.com";

  private final CdhService cdhService;
  private final CompanyMapper companyMapper;
  private final EmployeeMapper employeeMapper;
  private final UpsellItemsAllowedUtils upsellItemsAllowedUtils;
  private final Auth0Service auth0Service;

  public CompanyDetailsResponse getCompanyDetails(String companyId, String accessedBy) {

    var companyFuture = CompletableFuture.supplyAsync(
        () -> cdhService.getCompanyDetails(companyId, accessedBy));
    var employeesFuture = CompletableFuture.supplyAsync(
        () -> cdhService.getCompanyEmployees(companyId, accessedBy));

    var companyDetailsCdh = companyFuture.join();

    var companyDetailsResponse = companyMapper.toCompanyDetailsResponse(companyDetailsCdh);
    companyMapper.setManagementInformationAnswerIfNull(companyDetailsResponse);
    companyDetailsResponse.setSuccess(true);

    try {
      employeesFuture.join().ifPresent(employeesResponse -> {
        if (employeesResponse.getResults() != null
            && companyDetailsResponse.getRequestedCompany() != null
            && companyDetailsResponse.getRequestedCompany().getCompanyDetails() != null) {
          companyDetailsResponse.getRequestedCompany().getCompanyDetails()
              .setNumberOfEmployees(employeesResponse.getTotalEmployeesInCompany());
        }
      });
    } catch (Exception e) {
      log.warn("Failed to fetch company employees for companyId={}, keeping existing numberOfEmployees={}: {}",
          sanitizeInputString(companyId), e.getMessage());
    }

    calculateUpsellItemsAllowed(companyDetailsCdh, companyDetailsResponse);

    return companyDetailsResponse;
  }

  public void updateCompany(String companyId, CompanySummary companySummary, String userEmail) {
    GetCompanyResponse getCompanyResponse = cdhService.getCompanyDetails(companyId, userEmail);
    GetEmployeeResponse mainContactResponse =
        getMainContactResponse(companyId, companySummary, userEmail, getCompanyResponse);

    CompanyAccountRequest companyAccountRequest = companyMapper
        .toCompanyAccountRequest(getCompanyResponse);
    EmployeeAccountRequest mainContactRequest = employeeMapper.toEmployeeAccountRequest(mainContactResponse,
        companySummary.getMainContact());
    companyAccountRequest.setMainContact(mainContactRequest);
    CompanyAccountRequest updatedCompanyRequest = companyMapper
        .toUpdatedCompanyAccountRequest(companyAccountRequest, companySummary);

    auth0Service.updateUserDetailsInAuth0(mainContactResponse.getEmailAddress(),
        companySummary.getMainContact().getEmailAddress());
    cdhService.updateCompanyDetails(companyId, updatedCompanyRequest, userEmail);
  }

  public CheckCompanyResponse searchByCompanyNameAndAddress(String companyName, Address address) {
    GetCompaniesQueryParams queryParams = companyMapper
        .toGetCompaniesQueryParams(companyName, address);
    var existingCompany = !cdhService
        .getCompanyDetailsByCompanyNameAndAddress(queryParams, DUMMY_EMAIL).isEmpty();
    CheckCompanyResponse checkCompanyResponse = new CheckCompanyResponse();
    checkCompanyResponse.setExistingCompany(existingCompany);
    return checkCompanyResponse;
  }

  private void calculateUpsellItemsAllowed(GetCompanyResponse companyDetailsCdh,
      CompanyDetailsResponse companyDetailsResponse) {
    if (companyDetailsResponse.getRequestedCompany() != null
        && companyDetailsResponse.getRequestedCompany().getBookingAllowances() != null
        && companyDetailsCdh.getBookingAllowances() != null
        && companyDetailsCdh.getBookingAllowances().getExtrasCodes() != null) {
      companyDetailsResponse.getRequestedCompany().getBookingAllowances().setUpsellItemsAllowed(
          upsellItemsAllowedUtils.calculateUpsellItemsAllowed(
              companyDetailsCdh.getBookingAllowances().getExtrasCodes()));
    }
  }

  private GetEmployeeResponse getMainContactResponse(String companyId,
      CompanySummary companySummary, String userEmail, GetCompanyResponse getCompanyResponse) {
    return (getCompanyResponse.getMainContact() != null && getCompanyResponse.getMainContact()
        .getEmployeeAccountId()
        .equals(companySummary.getMainContact().getId()))
        ? getCompanyResponse.getMainContact()
        : cdhService.getEmployeeById(companyId, companySummary.getMainContact().getId(), userEmail);
  }
}
