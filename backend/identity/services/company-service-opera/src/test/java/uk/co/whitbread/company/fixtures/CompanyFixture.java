package uk.co.whitbread.company.fixtures;

import static uk.co.whitbread.company.fixtures.EmployeeFixture.buildGetEmployeeResponse;
import static uk.co.whitbread.company.fixtures.EmployeeFixture.buildMainContact;

import java.util.UUID;
import uk.co.whitbread.company.model.Address;
import uk.co.whitbread.company.model.CompanySummary;
import uk.co.whitbread.shared.cdh.model.company.GetCompanyResponse;

public class CompanyFixture {

  public static GetCompanyResponse buildGetCompanyResponse() {
    String companyAccountId = UUID.randomUUID().toString();
    return GetCompanyResponse.builder()
        .companyAccountId(companyAccountId)
        .companyName("Company Name")
        .mainContact(buildGetEmployeeResponse(companyAccountId))
        .build();
  }

  public static CompanySummary buildCompanySummary() {
    return CompanySummary.builder()
        .companyName("Company Name")
        .alternateCompanyName("Alternate Company Name")
        .companyAddress(Address.builder()
            .addressLine1("Address Line 1")
            .addressLine2("Address Line 2")
            .postCode("ABCD EFG")
            .country("UK")
            .build())
        .mainContact(buildMainContact())
        .build();
  }
}
