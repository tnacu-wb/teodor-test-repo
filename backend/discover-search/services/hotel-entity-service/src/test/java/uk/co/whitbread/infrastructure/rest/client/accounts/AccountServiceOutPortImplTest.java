package uk.co.whitbread.infrastructure.rest.client.accounts;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.infrastructure.rest.client.accounts.model.out.CellCode;
import uk.co.whitbread.infrastructure.rest.client.accounts.model.out.Company;
import uk.co.whitbread.infrastructure.rest.client.accounts.model.out.CompanyDetails;
import uk.co.whitbread.infrastructure.rest.client.accounts.model.out.CompanyDetailsResponse;
import uk.co.whitbread.infrastructure.rest.client.accounts.service.AccountServiceClient;

@ExtendWith(MockitoExtension.class)
class AccountServiceOutPortImplTest {

  private static final String COMPANY_NAME = "Company Name";
  private static final String BFLEX = "BFLEX";
  private static final String AUTHORIZATION = "1234567890abcdef";
  private static final String COMPANY_ID = "COMP_abcdef_1234_5678_90abcdef";

  @InjectMocks
  private AccountServiceOutPortImpl outPort;

  @Mock
  private AccountServiceClient accountServiceClient;


  @Test
  void getCompanyDetails() {
    //Arrange
    when(accountServiceClient.getCompanyDetails(anyString(), anyString()))
        .thenReturn(buildResponse());

    //Act
    var companyDetailsResponse = outPort.getCompanyDetails(AUTHORIZATION, COMPANY_ID);

    //Assert
    assertThat(companyDetailsResponse, notNullValue());
    assertThat(companyDetailsResponse.getRequestedCompany().getCompanyDetails().getCompanyName()
        , is(COMPANY_NAME));
    assertThat(companyDetailsResponse.getCompanyCellCodes().size(), is(1));
    assertThat(companyDetailsResponse.getCompanyCellCodes().get(0).getType(), is(BFLEX));
    verifyNoMoreInteractions(accountServiceClient);
  }

  private CompanyDetailsResponse buildResponse() {
    CompanyDetailsResponse response = new CompanyDetailsResponse();
    Company company = new Company();
    CompanyDetails companyDetails = new CompanyDetails();
    companyDetails.setCompanyName(COMPANY_NAME);
    company.setCompanyDetails(companyDetails);
    CellCode cellCode = new CellCode();
    cellCode.setType(BFLEX);
    company.setCompanyCellCodes(List.of(cellCode));
    response.setRequestedCompany(company);
    response.setCompanyCellCodes(List.of(cellCode));
    return response;
  }
}
