package uk.co.whitbread.company.service.cdh;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.company.fixtures.CompanyFixture.buildCompanySummary;
import static uk.co.whitbread.company.fixtures.CompanyFixture.buildGetCompanyResponse;
import static uk.co.whitbread.company.fixtures.EmployeeFixture.buildGetEmployeeResponse;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.co.whitbread.company.mapper.AddressMapper;
import uk.co.whitbread.company.mapper.CompanyMapper;
import uk.co.whitbread.company.mapper.EmployeeMapper;
import uk.co.whitbread.company.model.BookingAllowances;
import uk.co.whitbread.company.model.Company;
import uk.co.whitbread.company.model.CompanyDetails;
import uk.co.whitbread.company.model.CompanyDetailsResponse;
import uk.co.whitbread.company.service.Auth0Service;
import uk.co.whitbread.company.utils.UpsellItemsAllowedUtils;
import uk.co.whitbread.shared.cdh.model.GetEmployeesResponse;
import uk.co.whitbread.shared.cdh.model.company.CompanyAccountRequest;
import uk.co.whitbread.shared.cdh.model.company.GetCompanyResponse;

@ExtendWith(MockitoExtension.class)
class CdhCompanyDetailsServiceTest {

  private static final String EMAIL = "user@mail.com";
  private static final String COMPANY_ID = "companyId";

  @Mock
  private CdhService cdhService;
  @Mock
  private Auth0Service auth0Service;
  @Spy
  private CompanyMapper companyMapper = Mappers.getMapper(CompanyMapper.class);
  @Spy
  private EmployeeMapper employeeMapper = Mappers.getMapper(EmployeeMapper.class);
  @Spy
  private AddressMapper addressMapper = Mappers.getMapper(AddressMapper.class);
  @Mock
  private UpsellItemsAllowedUtils upsellItemsAllowedUtils;

  @InjectMocks
  private CdhCompanyDetailsService cdhCompanyDetailsService;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(companyMapper, "employeeMapper", employeeMapper);
    ReflectionTestUtils.setField(companyMapper, "addressMapper", addressMapper);
  }

  @Test
  void getCompanyDetails_success() {
    var companyDetailsCdh = GetCompanyResponse.builder().bookingAllowances(
        uk.co.whitbread.shared.cdh.model.company.BookingAllowances.builder()
            .extrasCodes(List.of("1, 2")).build()).build();

    var companyDetailsResponse = new CompanyDetailsResponse();
    companyDetailsResponse.setRequestedCompany(
        Company.builder().bookingAllowances(new BookingAllowances()).build());

    when(cdhService.getCompanyDetails(COMPANY_ID, EMAIL))
        .thenReturn(companyDetailsCdh);
    when(companyMapper.toCompanyDetailsResponse(any())).thenReturn(companyDetailsResponse);
    when(upsellItemsAllowedUtils.calculateUpsellItemsAllowed(any())).thenReturn(
        List.of("11", "15"));

    var response = cdhCompanyDetailsService.getCompanyDetails(COMPANY_ID, EMAIL);

    verify(companyMapper).toCompanyDetailsResponse(any());
    verify(cdhService).getCompanyDetails(any(), any());
    assertNotNull(response);
  }

  @Test
  void searchByCompanyNameAndAddress_success() {
    when(cdhService.getCompanyDetailsByCompanyNameAndAddress(any(), any()))
        .thenReturn(List.of(GetCompanyResponse.builder().build()));

    var response = cdhCompanyDetailsService.searchByCompanyNameAndAddress(any(), any());

    verify(companyMapper).toGetCompaniesQueryParams(any(), any());
    verify(cdhService).getCompanyDetailsByCompanyNameAndAddress(any(), any());
    assertTrue(response.isExistingCompany());
  }

  @Test
  void searchByCompanyNameAndAddressWhenCompanyNotFound_success() {

    var response = cdhCompanyDetailsService.searchByCompanyNameAndAddress(any(), any());

    verify(companyMapper).toGetCompaniesQueryParams(any(), any());
    verify(cdhService).getCompanyDetailsByCompanyNameAndAddress(any(), any());
    assertFalse(response.isExistingCompany());
  }

  @Test
  void updateCompany_mainContactNotChanged_success() {
    var getCompanyResponse = buildGetCompanyResponse();
    var companySummary = buildCompanySummary();
    var companyAccountId = getCompanyResponse.getCompanyAccountId();
    var mainContact = companySummary.getMainContact();
    mainContact.setId(getCompanyResponse.getMainContact().getEmployeeAccountId());
    mainContact.setEmailAddress(getCompanyResponse.getMainContact().getEmailAddress());

    when(cdhService.getCompanyDetails(companyAccountId, EMAIL))
        .thenReturn(getCompanyResponse);

    cdhCompanyDetailsService.updateCompany(companyAccountId, companySummary, EMAIL);

    var argumentCaptor = ArgumentCaptor.forClass(CompanyAccountRequest.class);
    verify(auth0Service).updateUserDetailsInAuth0(getCompanyResponse.getMainContact().getEmailAddress(),
        companySummary.getMainContact().getEmailAddress());
    verify(cdhService).updateCompanyDetails(eq(companyAccountId), argumentCaptor.capture(), eq(EMAIL));

    var updatedCompanyRequest = argumentCaptor.getValue();
    assertEquals(getCompanyResponse.getMainContact().getEmployeeAccountId(),
        updatedCompanyRequest.getMainContact().getEmployeeAccountId());
    assertEquals(getCompanyResponse.getMainContact().getEmailAddress(),
        updatedCompanyRequest.getMainContact().getEmailAddress());
  }

  @Test
  void updateCompany_mainContactChanged_success() {
    var getCompanyResponse = buildGetCompanyResponse();
    var companySummary = buildCompanySummary();
    var companyAccountId = getCompanyResponse.getCompanyAccountId();
    var newMainContact = buildGetEmployeeResponse(companyAccountId);
    newMainContact.setEmployeeAccountId(companySummary.getMainContact().getId());

    when(cdhService.getEmployeeById(companyAccountId, companySummary.getMainContact().getId(), EMAIL))
        .thenReturn(newMainContact);
    when(cdhService.getCompanyDetails(companyAccountId, EMAIL))
        .thenReturn(getCompanyResponse);

    cdhCompanyDetailsService.updateCompany(companyAccountId, companySummary, EMAIL);

    var argumentCaptor = ArgumentCaptor.forClass(CompanyAccountRequest.class);
    verify(auth0Service).updateUserDetailsInAuth0(getCompanyResponse.getMainContact().getEmailAddress(),
        companySummary.getMainContact().getEmailAddress());
    verify(cdhService).updateCompanyDetails(eq(companyAccountId), argumentCaptor.capture(), eq(EMAIL));

    var updatedCompanyRequest = argumentCaptor.getValue();
    assertNotEquals(getCompanyResponse.getMainContact().getEmployeeAccountId(),
        updatedCompanyRequest.getMainContact().getEmployeeAccountId());
    assertNotEquals(getCompanyResponse.getMainContact().getEmailAddress(),
        updatedCompanyRequest.getMainContact().getEmailAddress());
  }

  @Test
  void getCompanyDetails_numberOfEmployeesOverwritten() {
    var companyDetailsCdh = GetCompanyResponse.builder().build();
    var companyDetails = new CompanyDetails();
    companyDetails.setNumberOfEmployees(3);
    var companyDetailsResponse = new CompanyDetailsResponse();
    companyDetailsResponse.setRequestedCompany(Company.builder().companyDetails(companyDetails).build());

    when(cdhService.getCompanyDetails(COMPANY_ID, EMAIL)).thenReturn(companyDetailsCdh);
    when(companyMapper.toCompanyDetailsResponse(any())).thenReturn(companyDetailsResponse);
    when(cdhService.getCompanyEmployees(COMPANY_ID, EMAIL))
        .thenReturn(Optional.of(GetEmployeesResponse.builder()
            .results(List.of())
            .totalEmployeesInCompany(10)
            .build()));

    var response = cdhCompanyDetailsService.getCompanyDetails(COMPANY_ID, EMAIL);

    assertEquals(10, response.getRequestedCompany().getCompanyDetails().getNumberOfEmployees());
  }

  @Test
  void getCompanyDetails_employeesCallFails_keepsExistingNumberOfEmployees() {
    var companyDetailsCdh = GetCompanyResponse.builder().build();
    var companyDetails = new CompanyDetails();
    companyDetails.setNumberOfEmployees(5);
    var companyDetailsResponse = new CompanyDetailsResponse();
    companyDetailsResponse.setRequestedCompany(Company.builder().companyDetails(companyDetails).build());

    when(cdhService.getCompanyDetails(COMPANY_ID, EMAIL)).thenReturn(companyDetailsCdh);
    when(companyMapper.toCompanyDetailsResponse(any())).thenReturn(companyDetailsResponse);
    when(cdhService.getCompanyEmployees(COMPANY_ID, EMAIL))
        .thenThrow(new RuntimeException("CDH unavailable"));

    var response = cdhCompanyDetailsService.getCompanyDetails(COMPANY_ID, EMAIL);

    assertNotNull(response);
    assertEquals(5, response.getRequestedCompany().getCompanyDetails().getNumberOfEmployees());
  }

  @Test
  void getCompanyDetails_employeesReturnsEmpty_numberOfEmployeesNotOverwritten() {
    var companyDetailsCdh = GetCompanyResponse.builder().build();
    var companyDetails = new CompanyDetails();
    companyDetails.setNumberOfEmployees(7);
    var companyDetailsResponse = new CompanyDetailsResponse();
    companyDetailsResponse.setRequestedCompany(Company.builder().companyDetails(companyDetails).build());

    when(cdhService.getCompanyDetails(COMPANY_ID, EMAIL)).thenReturn(companyDetailsCdh);
    when(companyMapper.toCompanyDetailsResponse(any())).thenReturn(companyDetailsResponse);
    when(cdhService.getCompanyEmployees(COMPANY_ID, EMAIL)).thenReturn(Optional.empty());

    var response = cdhCompanyDetailsService.getCompanyDetails(COMPANY_ID, EMAIL);

    assertEquals(7, response.getRequestedCompany().getCompanyDetails().getNumberOfEmployees());
  }
}
