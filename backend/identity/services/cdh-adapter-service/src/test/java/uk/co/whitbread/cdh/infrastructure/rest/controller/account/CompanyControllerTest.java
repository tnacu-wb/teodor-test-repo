package uk.co.whitbread.cdh.infrastructure.rest.controller.account;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.cdh.domain.model.account.in.CompanySearchCriteria;
import uk.co.whitbread.cdh.domain.model.account.out.Company;
import uk.co.whitbread.cdh.domain.model.account.out.CompanySearch;
import uk.co.whitbread.cdh.domain.ports.primary.CompanyInPort;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.mapper.CompanyResponseMapper;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.mapper.CompanySearchCriteriaRequestMapper;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.mapper.CompanySearchResponseMapper;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.in.AccessRequestDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.in.CompanySearchCriteriaDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out.CompanyDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out.CompanySearchResponseDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out.CompanySuppressRatesDto;

@ExtendWith(MockitoExtension.class)
class CompanyControllerTest {

  @InjectMocks
  CompanyController companyController;

  @Mock
  CompanySearchCriteriaRequestMapper companySearchCriteriaRequestMapper;

  @Mock
  CompanyInPort companyInPort;

  @Mock
  CompanySearchResponseMapper companySearchResponseMapper;

  @Mock
  CompanyResponseMapper companyResponseMapper;

  @Test
  void test_getCompany() {
    String companyAccountId = "12345";
    AccessRequestDto accessRequestDto = AccessRequestDto.builder()
        .accessContext("test-access-context")
        .accessedBy("junit")
        .build();

    Company company = Company.builder().companyAccountId(companyAccountId).build();
    CompanyDto companyDto = CompanyDto.builder().companyAccountId(companyAccountId).build();

    when(companyInPort.getCompany(companyAccountId, "test-access-context", "junit"))
        .thenReturn(company);
    when(companyResponseMapper.toDto(company))
        .thenReturn(companyDto);

    var response = companyController.getCompany(companyAccountId, accessRequestDto);

    assertNotNull(response);
    assertEquals(companyAccountId, response.getCompanyAccountId());
    verify(companyInPort).getCompany(companyAccountId, "test-access-context", "junit");
    verify(companyResponseMapper).toDto(company);
  }

  @Test
  void test_getCompanySuppressRates() {
    String companyAccountId = "12345";
    AccessRequestDto accessRequestDto = AccessRequestDto.builder()
        .accessContext("test-access-context")
        .accessedBy("junit")
        .build();

    List<String> suppressRates = List.of("RATE_A", "RATE_B");
    Company company = Company.builder().companyAccountId(companyAccountId).build();
    CompanyDto companyDto = CompanyDto.builder()
        .companyAccountId(companyAccountId)
        .suppressRates(suppressRates)
        .build();

    when(companyInPort.getCompany(companyAccountId, "test-access-context", "junit"))
        .thenReturn(company);
    when(companyResponseMapper.toDto(company))
        .thenReturn(companyDto);

    CompanySuppressRatesDto response = companyController.getCompanySuppressRates(companyAccountId, accessRequestDto);

    assertNotNull(response);
    assertEquals(suppressRates, response.suppressRates());
    verify(companyInPort).getCompany(companyAccountId, "test-access-context", "junit");
    verify(companyResponseMapper).toDto(company);
  }

  @Test
  void test_getCompanySuppressRates_whenNullSuppressRates_returnsNullList() {
    String companyAccountId = "12345";
    AccessRequestDto accessRequestDto = AccessRequestDto.builder()
        .accessContext("test-access-context")
        .accessedBy("junit")
        .build();

    Company company = Company.builder().companyAccountId(companyAccountId).build();
    CompanyDto companyDto = CompanyDto.builder()
        .companyAccountId(companyAccountId)
        .suppressRates(null)
        .build();

    when(companyInPort.getCompany(companyAccountId, "test-access-context", "junit"))
        .thenReturn(company);
    when(companyResponseMapper.toDto(company))
        .thenReturn(companyDto);

    CompanySuppressRatesDto response = companyController.getCompanySuppressRates(companyAccountId, accessRequestDto);

    assertNotNull(response);
    assertNull(response.suppressRates());
    verify(companyInPort).getCompany(companyAccountId, "test-access-context", "junit");
    verify(companyResponseMapper).toDto(company);
  }

  @Test
  void test_getCompanies() {
    CompanySearchCriteriaDto companySearchCriteriaDto =  CompanySearchCriteriaDto
        .builder()
        .companyName("test")
        .accessContext("test-access-context")
        .accessedBy("junit")
        .globalCompanyId(123)
        .companyType("BB")
        .addressLine1("address one")
        .addressLine2("address two")
        .addressLine3("address three")
        .addressLine4("address four")
        .addressLine5("address five")
        .cellCode("cell code")
        .countryCode("country code")
        .pageSize(1)
        .postCode("post code")
        .pageNumber(0)
        .sortBy("companyName")
        .sortDirection("asc")
        .build();

    CompanySearchCriteria companySearchCriteria = CompanySearchCriteria
        .builder()
        .companyName("test")
        .build();

    when(companySearchCriteriaRequestMapper.toModel(companySearchCriteriaDto))
        .thenReturn(companySearchCriteria);
    when(companyInPort.getCompanies(companySearchCriteria))
        .thenReturn(CompanySearch.builder().build());
    when(companySearchResponseMapper.toDto(CompanySearch.builder().build()))
        .thenReturn(CompanySearchResponseDto.builder().build());

    var response = companyController.getCompanies(companySearchCriteriaDto);
    assertNotNull(response);

  }
}