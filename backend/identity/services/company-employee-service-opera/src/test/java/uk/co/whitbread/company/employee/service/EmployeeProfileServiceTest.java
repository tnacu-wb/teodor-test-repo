package uk.co.whitbread.company.employee.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.company.employee.exceptions.EmployeeNotFoundException.ERROR_CODE;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.co.whitbread.company.employee.exceptions.EmployeeNotFoundException;
import uk.co.whitbread.company.employee.mapper.BookingPreferenceMapper;
import uk.co.whitbread.company.employee.mapper.EmployeeMapper;
import uk.co.whitbread.company.employee.model.AccessLevel;
import uk.co.whitbread.company.employee.model.BookingPreference;
import uk.co.whitbread.company.employee.model.EmployeeStatus;
import uk.co.whitbread.company.employee.model.RoomType;
import uk.co.whitbread.company.employee.model.UpdateRegistrationRequest;
import uk.co.whitbread.company.employee.model.feature.FeatureFlag;
import uk.co.whitbread.company.employee.model.feature.UnleashWrapper;
import uk.co.whitbread.company.employee.properties.EmailProperties;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.cdh.EmployeeDataService;
import uk.co.whitbread.shared.cdh.model.EmployeeAccountRequest;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;
import uk.co.whitbread.shared.cdh.model.GetEmployeesResponse;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EmployeeProfileServiceTest {

    private static final String COMPANY_ID = "42";
    private static final String EMPLOYEE_ID = "employeeId";
    private static final String EMAIL = "email@test.com";
    private static final String LANGUAGE_DE = "de";

    @Mock
    private Auth0Service mockAuth0Service;
    @Mock
    private EmployeeMapper mockEmployeeMapper;
    @Mock
    private EmailProperties mockEmailProperties;
  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;

    @InjectMocks
    private EmployeeProfileService sut;

    @Mock
    private BookingPreferenceMapper mockBookingPreferenceMapper;

    @Mock
    private EmployeeDataService mockEmployeeDataService;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        var mockedFeatureFlag = org.mockito.Mockito.mock(FeatureFlag.class);
        when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
        when(unleashWrapper.isEnabled(any())).thenReturn(false);
        when(mockEmailProperties.getLoginUrl()).thenReturn("loginUrlEn");
        when(mockEmailProperties.getLoginUrlDe()).thenReturn("loginUrlDe");
    }

    @Test
    void updateCdhRegistration_shouldMakeRequest() {
        // Given
        CdhEmployeeDetails travelManager = cdhEmployeeDetailsBuilder();
        UpdateRegistrationRequest request = new UpdateRegistrationRequest();
        request.setEmailAddress(EMAIL);
        request.setApproved(true);
        request.setAccessLevel(AccessLevel.SUPER);
        List<GetEmployeeResponse> getEmployeeResponse = List.of(GetEmployeeResponse.builder().build());
        var getEmployeesResponse = GetEmployeesResponse.builder().results(getEmployeeResponse)
            .build();

        when(mockEmployeeDataService.getEmployees(any(), any())).thenReturn(Optional.of(getEmployeesResponse));
        when(mockEmployeeMapper.toEmployeeAccountRequest(getEmployeeResponse.get(0)))
            .thenReturn(EmployeeAccountRequest.builder().build());

        // When
        sut.updateCdhRegistration(request, travelManager, LANGUAGE_DE);

        // Then
        verify(mockEmployeeDataService).getEmployees(any(), any());
        verify(mockEmployeeMapper).toEmployeeAccountRequest(getEmployeeResponse.get(0));
        verify(mockEmployeeDataService).updateEmployeeAccount(any(), any(), any(), any());
        verify(mockAuth0Service).updateAppMetadata(any(),any());
    }


    @Test
    void updateCdhRegistration_whenApprovedIsFalse_shouldDeleteUser() throws Exception {
        // Given
        CdhEmployeeDetails travelManager = cdhEmployeeDetailsBuilder();
        UpdateRegistrationRequest request = new UpdateRegistrationRequest();
        request.setEmailAddress(EMAIL);
        request.setApproved(false);
        request.setAccessLevel(AccessLevel.SUPER);
        List<GetEmployeeResponse> getEmployeeResponse = List.of(GetEmployeeResponse.builder().build());
        var getEmployeesResponse = GetEmployeesResponse.builder().results(getEmployeeResponse)
            .build();

        when(mockEmployeeDataService.getEmployees(any(), any())).thenReturn(Optional.of(getEmployeesResponse));

        // When
        sut.updateCdhRegistration(request, travelManager, LANGUAGE_DE);

        // Then
        verify(mockEmployeeDataService).getEmployees(any(), any());
        verify(mockEmployeeDataService).deleteEmployeeAccount(any(), any(), any());
        verify(mockAuth0Service).deleteUser(any());
    }

    @Test
    void updateCdhRegistration_whenCdhReturnsEmptyList_shouldThrowEmployeeNotFoundException() {
        // Given
        CdhEmployeeDetails travelManager = cdhEmployeeDetailsBuilder();
        UpdateRegistrationRequest request = new UpdateRegistrationRequest();
        request.setEmailAddress(EMAIL);
        request.setApproved(true);
        request.setAccessLevel(AccessLevel.SUPER);
        List<GetEmployeeResponse> getEmployeeResponse = List.of(GetEmployeeResponse.builder().build());
        when(mockEmployeeDataService.getEmployees(any(), any())).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> sut.updateCdhRegistration(request, travelManager, LANGUAGE_DE))
            .isInstanceOf(EmployeeNotFoundException.class)
            .hasFieldOrPropertyWithValue("errorCode", ERROR_CODE)
            .hasMessageContaining("was not found");
        verify(mockEmployeeDataService).getEmployees(any(), any());
        verify(mockEmployeeMapper, times(0)).toEmployeeAccountRequest(getEmployeeResponse.get(0));
        verify(mockEmployeeDataService, times(0)).updateEmployeeAccount(any(), any(), any(), any());
        verify(mockAuth0Service, times(0)).updateAppMetadata(any(),any());
    }

    @Test
    void updateCdhRegistration_whenActivateEmployeeThrowsException_shouldThrowEmployeeNotFoundException() {
        // Given
        CdhEmployeeDetails travelManager = cdhEmployeeDetailsBuilder();
        UpdateRegistrationRequest request = new UpdateRegistrationRequest();
        request.setEmailAddress(EMAIL);
        request.setApproved(true);
        request.setAccessLevel(AccessLevel.SUPER);
        List<GetEmployeeResponse> getEmployeeResponse = List.of(
            GetEmployeeResponse.builder().activationKey("AC")
                .employeeStatus(EmployeeStatus.INACTIVE.getValue()).build());
        when(mockEmployeeDataService.getEmployees(any(), any())).thenReturn(
            Optional.of(GetEmployeesResponse.builder().results(getEmployeeResponse).build()));
        when(mockEmployeeDataService.activateEmployee(any(), any(), any())).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> sut.updateCdhRegistration(request, travelManager, LANGUAGE_DE))
            .isInstanceOf(EmployeeNotFoundException.class)
            .hasFieldOrPropertyWithValue("errorCode", ERROR_CODE)
            .hasMessageContaining("Could not activate employee:");
        verify(mockEmployeeDataService).getEmployees(any(), any());
        verify(mockEmployeeMapper, times(0)).toEmployeeAccountRequest(getEmployeeResponse.get(0));
        verify(mockEmployeeDataService, times(0)).updateEmployeeAccount(any(), any(), any(), any());
        verify(mockAuth0Service, times(0)).updateAppMetadata(any(),any());
    }

    @Test
    void updateCdhRegistration_activateEmployeeReturnsSuccessfully_shouldMakeRequest() {
        // Given
        CdhEmployeeDetails travelManager = cdhEmployeeDetailsBuilder();
        UpdateRegistrationRequest request = new UpdateRegistrationRequest();
        request.setEmailAddress(EMAIL);
        request.setApproved(true);
        request.setAccessLevel(AccessLevel.SUPER);
        List<GetEmployeeResponse> getEmployeeResponse = List.of(
            GetEmployeeResponse.builder().activationKey("AC")
                .employeeStatus(EmployeeStatus.INACTIVE.getValue()).build());
        var getEmployeesResponse = GetEmployeesResponse.builder().results(getEmployeeResponse)
            .build();
        when(mockEmployeeDataService.getEmployees(any(), any())).thenReturn(Optional.of(getEmployeesResponse));
        when(mockEmployeeDataService.activateEmployee(any(), any(), any())).thenReturn(Optional.of(getEmployeeResponse.get(0)));
        when(mockEmployeeMapper.toEmployeeAccountRequest(getEmployeeResponse.get(0)))
            .thenReturn(EmployeeAccountRequest.builder().build());

        // When
        sut.updateCdhRegistration(request, travelManager, LANGUAGE_DE);

        // Then
        verify(mockEmployeeDataService).getEmployees(any(), any());
        verify(mockEmployeeMapper).toEmployeeAccountRequest(getEmployeeResponse.get(0));
        verify(mockEmployeeDataService).updateEmployeeAccount(any(), any(), any(), any());
        verify(mockAuth0Service).updateAppMetadata(any(),any());
    }

    @Test
    void getCdhEmployeeBookingPreferences_Success() throws IOException {

        // Given
        BookingPreference bookingPreference = BookingPreference.builder().mealDeal(true).children(2).adults(2).build();

        var getEmployeeResponse = objectMapper.readValue(
                new File("src/test/resources/mapping/CdhGetEmployeeResponse.json"),
                uk.co.whitbread.shared.cdh.model.GetEmployeeResponse.class);

        when(mockEmployeeDataService.getEmployee(COMPANY_ID, EMPLOYEE_ID, EMAIL)).thenReturn(Optional.of(getEmployeeResponse));
        when(mockBookingPreferenceMapper.toBookingPreference(getEmployeeResponse.getBookingPreference())).thenReturn(bookingPreference);

        //When
        BookingPreference bookingPreferenceResponse = sut.getCdhEmployeeBookingPreferences(COMPANY_ID, EMPLOYEE_ID, EMAIL);

        // Then
        assertNotNull(bookingPreferenceResponse);
        assertEquals(bookingPreferenceResponse,bookingPreference);
    }

    @Test
    void getCdhEmployeeBookingPreferences_ThrowsEmployeeNotFoundException() {

        // Given
        when(mockEmployeeDataService.getEmployee(COMPANY_ID, EMPLOYEE_ID, EMAIL)).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> sut.getCdhEmployeeBookingPreferences(COMPANY_ID, EMPLOYEE_ID, EMAIL))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", ERROR_CODE)
                .hasMessageContaining(String.format("Employee %s from company %s was not found", EMPLOYEE_ID, COMPANY_ID));
    }

    @Test
    void updateEmployeeBookingPreferences_Success() throws IOException {
        BookingPreference bookingPreference = BookingPreference.builder().mealDeal(true)
            .children(2).adults(2).cotRequired(false).premierBreakfast(false)
            .roomType(RoomType.DB).preselectWifi(false).electronicInvoice(false)
            .continentalBreakfast(false)
            .build();

        var getEmployeeResponse = objectMapper.readValue(
            new File("src/test/resources/mapping/CdhGetEmployeeResponse.json"),
            uk.co.whitbread.shared.cdh.model.GetEmployeeResponse.class);
        var employeeRequest = objectMapper.readValue(
            new File("src/test/resources/mapping/EmployeeAccountRequest.json"),
            uk.co.whitbread.shared.cdh.model.EmployeeAccountRequest.class);

        when(mockEmployeeDataService.getEmployee(COMPANY_ID, EMPLOYEE_ID, EMAIL)).thenReturn(
            Optional.of(getEmployeeResponse));
        when(mockEmployeeMapper.toEmployeeAccountRequest(getEmployeeResponse)).thenReturn(
            employeeRequest);

        sut.updateCdhEmployeeBookingPreferences(COMPANY_ID, EMPLOYEE_ID, bookingPreference, EMAIL);

        verify(mockEmployeeDataService).updateEmployeeAccount(COMPANY_ID, EMPLOYEE_ID,
            employeeRequest, EMAIL);
    }

  @Test
  void updateEmployeeBookingPreferences_EmployeeNotFound() {
    // Given
    BookingPreference bookingPreference = BookingPreference.builder().build();

    // When/Then
    assertThatThrownBy(() -> sut.updateCdhEmployeeBookingPreferences(COMPANY_ID, EMPLOYEE_ID, bookingPreference, EMAIL))
        .isInstanceOf(EmployeeNotFoundException.class);
    verify(mockEmployeeDataService, times(0)).updateEmployeeAccount(eq(COMPANY_ID),
        eq(EMPLOYEE_ID), any(EmployeeAccountRequest.class), eq(EMAIL));
  }

  @Test
  void updateCdhRegistration_withFeatureFlagEnabled_shouldMakeRequest() {
    // Given
    CdhEmployeeDetails travelManager = cdhEmployeeDetailsBuilder();
    UpdateRegistrationRequest request = new UpdateRegistrationRequest();
    request.setEmailAddress(EMAIL);
    request.setApproved(true);
    request.setAccessLevel(AccessLevel.SUPER);
    List<GetEmployeeResponse> getEmployeeResponse = List.of(GetEmployeeResponse.builder().build());
    var getEmployeesResponse = GetEmployeesResponse.builder().results(getEmployeeResponse)
        .build();

    when(unleashWrapper.isEnabled(any())).thenReturn(true);
    when(mockEmployeeDataService.getEmployeesV2(any(), any())).thenReturn(
        Optional.of(getEmployeesResponse));
    when(mockEmployeeMapper.toEmployeeAccountRequest(getEmployeeResponse.get(0)))
        .thenReturn(EmployeeAccountRequest.builder().build());

    // When
    sut.updateCdhRegistration(request, travelManager, LANGUAGE_DE);

    // Then
    verify(mockEmployeeDataService).getEmployeesV2(any(), any());
    verify(mockEmployeeMapper).toEmployeeAccountRequest(getEmployeeResponse.get(0));
    verify(mockEmployeeDataService).updateEmployeeAccount(any(), any(), any(), any());
    verify(mockAuth0Service).updateAppMetadata(any(), any());
  }

  @Test
  void updateCdhRegistration_withFeatureFlagEnabled_whenCdhReturnsEmptyList_shouldThrowEmployeeNotFoundException() {
    // Given
    CdhEmployeeDetails travelManager = cdhEmployeeDetailsBuilder();
    UpdateRegistrationRequest request = new UpdateRegistrationRequest();
    request.setEmailAddress(EMAIL);
    request.setApproved(true);
    request.setAccessLevel(AccessLevel.SUPER);

    when(unleashWrapper.isEnabled(any())).thenReturn(true);
    when(mockEmployeeDataService.getEmployeesV2(any(), any())).thenReturn(Optional.empty());

    // Then
    assertThatThrownBy(() -> sut.updateCdhRegistration(request, travelManager, LANGUAGE_DE))
        .isInstanceOf(EmployeeNotFoundException.class)
        .hasFieldOrPropertyWithValue("errorCode", ERROR_CODE)
        .hasMessageContaining("was not found");
    verify(mockEmployeeDataService).getEmployeesV2(any(), any());
    verify(mockEmployeeDataService, times(0)).updateEmployeeAccount(any(), any(), any(), any());
  }

  @Test
  void getCdhEmployeeBookingPreferences_withFeatureFlagEnabled_Success() throws IOException {

    // Given
    BookingPreference bookingPreference = BookingPreference.builder().mealDeal(true).children(2).adults(2).build();

    var getEmployeeResponse = objectMapper.readValue(
        new File("src/test/resources/mapping/CdhGetEmployeeResponse.json"),
        uk.co.whitbread.shared.cdh.model.GetEmployeeResponse.class);

    when(unleashWrapper.isEnabled(any())).thenReturn(true);
    when(mockEmployeeDataService.getEmployeeV2(COMPANY_ID, EMPLOYEE_ID, EMAIL)).thenReturn(Optional.of(getEmployeeResponse));
    when(mockBookingPreferenceMapper.toBookingPreference(getEmployeeResponse.getBookingPreference())).thenReturn(bookingPreference);

    //When
    BookingPreference bookingPreferenceResponse = sut.getCdhEmployeeBookingPreferences(COMPANY_ID, EMPLOYEE_ID, EMAIL);

    // Then
    assertNotNull(bookingPreferenceResponse);
    assertEquals(bookingPreferenceResponse,bookingPreference);
  }

  @Test
  void updateEmployeeBookingPreferences_withFeatureFlagEnabled_Success() throws IOException {
    BookingPreference bookingPreference = BookingPreference.builder().mealDeal(true)
        .children(2).adults(2).cotRequired(false).premierBreakfast(false)
        .roomType(RoomType.DB).preselectWifi(false).electronicInvoice(false)
        .continentalBreakfast(false)
        .build();

    var getEmployeeResponse = objectMapper.readValue(
        new File("src/test/resources/mapping/CdhGetEmployeeResponse.json"),
        uk.co.whitbread.shared.cdh.model.GetEmployeeResponse.class);
    var employeeRequest = objectMapper.readValue(
        new File("src/test/resources/mapping/EmployeeAccountRequest.json"),
        uk.co.whitbread.shared.cdh.model.EmployeeAccountRequest.class);

    when(unleashWrapper.isEnabled(any())).thenReturn(true);
    when(mockEmployeeDataService.getEmployeeV2(COMPANY_ID, EMPLOYEE_ID, EMAIL)).thenReturn(
        Optional.of(getEmployeeResponse));
    when(mockEmployeeMapper.toEmployeeAccountRequest(getEmployeeResponse)).thenReturn(
        employeeRequest);

    sut.updateCdhEmployeeBookingPreferences(COMPANY_ID, EMPLOYEE_ID, bookingPreference, EMAIL);

    verify(mockEmployeeDataService).updateEmployeeAccount(COMPANY_ID, EMPLOYEE_ID,
        employeeRequest, EMAIL);
  }

  private CdhEmployeeDetails cdhEmployeeDetailsBuilder() {
    return CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ID)
        .employeeAccountId(EMPLOYEE_ID)
        .userEmail(EMAIL).build();
  }
}
