package uk.co.whitbread.piba.account.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.piba.account.exception.InValidTokenException;
import uk.co.whitbread.piba.account.exception.RegisterTetheredUserException;
import uk.co.whitbread.piba.account.mapper.PibaTetheredGuidResponseMapper;
import uk.co.whitbread.piba.account.mapper.TetheredMapper;
import uk.co.whitbread.piba.account.model.TetheredGuidDetails;
import uk.co.whitbread.piba.account.model.TetheredUserRequest;
import uk.co.whitbread.piba.account.model.enums.Scheme;
import uk.co.whitbread.piba.account.util.TestUtil;
import uk.co.whitbread.piba.account.validation.PibaGuidValidator;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;
import uk.co.whitbread.shared.cdh.EmployeeDataService;
import uk.co.whitbread.shared.cdh.RegistrationDataService;
import uk.co.whitbread.shared.cdh.exception.CDHException;
import uk.co.whitbread.shared.cdh.model.PibaTetheredGuidResponse;

@ExtendWith(MockitoExtension.class)
class CdhRegistrationServiceTest {

  private static final String COMPANY_ID = "55";
  private static final String EMPLOYEE_ID = "22";
  private static final String USER_EMAIL = "InnBusiness@whitbread.com";
  private static final String TETHERED_GUID = "327f7a0c-9a33-41c2-808d-74f15f24797c";
  private static final String SCHEME_GB = "GB";

  @InjectMocks
  private CdhRegistrationService cdhRegistrationService;

  @Mock
  private RegistrationDataService registrationDataService;

  @Mock
  private PibaTetheredGuidResponseMapper pibaTetheredGuidResponseMapper;

  @Mock
  private PibaGuidValidator pibaGuidValidator;

  @Mock
  private EmployeeDataService employeeDataService;

  @Mock
  private TetheredMapper tetheredMapper;

  @Mock
  private TokenService tokenService;

  private final TestUtil testUtil = new TestUtil();

  @Test
  void getTetheredGuids_ShouldReturnOk() {
    // Arrange
    List<PibaTetheredGuidResponse> cdhResponse = createCdhPibaTetheredGuidResponse();
    when(registrationDataService.getDashboardDetails(any(), any(), any())).thenReturn(cdhResponse);
    List<uk.co.whitbread.piba.account.model.PibaTetheredGuidResponse> mappedResponse = testUtil.createPibaTetheredGuidResponseMultiple(COMPANY_ID, EMPLOYEE_ID);
    when(pibaTetheredGuidResponseMapper.map(cdhResponse)).thenReturn(mappedResponse);

    // Act
    var responseList = cdhRegistrationService.getTetheredGuids(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL);

    // Assert
    assertThat(responseList, notNullValue());
    assertEquals(TETHERED_GUID, responseList.get(0).getTetheredGuid().get(0));
    assertEquals(COMPANY_ID, responseList.get(0).getCompanyId());
    assertEquals(EMPLOYEE_ID, responseList.get(0).getEmployeeId());
    assertEquals(SCHEME_GB, responseList.get(0).getScheme().name());

    assertEquals(TETHERED_GUID, Mappers.getMapper(PibaTetheredGuidResponseMapper.class).map(cdhResponse).get(0).getTetheredGuid().get(0));

    verify(pibaTetheredGuidResponseMapper).map(any());
  }

  @Test
  void registerTetheredUser_ShouldReturnOk() {
    // Arrange
    var tetheredUserRequest = new TetheredUserRequest(Scheme.GB, COMPANY_ID,
        List.of(new TetheredGuidDetails(EMPLOYEE_ID, TETHERED_GUID)));
    doNothing().when(employeeDataService).registerTetheredUser(any(), any(), any());
    when(tetheredMapper.toTetheredUserRequest(any())).thenReturn(
        new uk.co.whitbread.shared.cdh.model.TetheredUserRequest(COMPANY_ID, SCHEME_GB,
            List.of(new uk.co.whitbread.shared.cdh.model.TetheredGuid(EMPLOYEE_ID, TETHERED_GUID))));
    when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(anyString()))
        .thenReturn(createCdhEmployeeDetailsResponse());

    // Act
    cdhRegistrationService.registerTetheredUser("Bearer token", tetheredUserRequest);

    // Assert
    verify(employeeDataService, atLeastOnce()).registerTetheredUser(any(), any(), any());
  }

  @Test
  void registerTetheredUser_ShouldThrowException() {
    // Arrange
    var tetheredUserRequest = new TetheredUserRequest(Scheme.GB, COMPANY_ID,
        List.of(new TetheredGuidDetails(EMPLOYEE_ID, TETHERED_GUID)));
    when(employeeDataService.registerTetheredUser(any(), any(), any())).thenThrow(new CDHException().addStatus(400));
    when(tetheredMapper.toTetheredUserRequest(any())).thenReturn(
        new uk.co.whitbread.shared.cdh.model.TetheredUserRequest(COMPANY_ID, SCHEME_GB,
            List.of(new uk.co.whitbread.shared.cdh.model.TetheredGuid(EMPLOYEE_ID, TETHERED_GUID))));
    when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(anyString()))
        .thenReturn(createCdhEmployeeDetailsResponse());

    // Act & Assert
    assertThrows(RegisterTetheredUserException.class,
        () ->cdhRegistrationService.registerTetheredUser("Bearer token", tetheredUserRequest));

  }

  @Test
  void registerTetheredUser_ShouldThrowExceptionInvalidTokenException() {
    // Arrange
    var tetheredUserRequest = new TetheredUserRequest(Scheme.GB, COMPANY_ID,
        List.of(new TetheredGuidDetails(EMPLOYEE_ID, TETHERED_GUID)));
    when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(anyString()))
        .thenReturn(CdhEmployeeDetails.builder().build());

    // Act & Assert
    assertThrows(InValidTokenException.class,
        () ->cdhRegistrationService.registerTetheredUser("Bearer token", tetheredUserRequest));

  }


  List<PibaTetheredGuidResponse> createCdhPibaTetheredGuidResponse() {
    return List.of(PibaTetheredGuidResponse.builder()
            .tetheredGuid(TETHERED_GUID)
            .companyId(55)
            .employeeId(22)
            .scheme(SCHEME_GB)
        .build());
  }

  CdhEmployeeDetails createCdhEmployeeDetailsResponse() {
    return CdhEmployeeDetails.builder()
        .employeeAccountId(EMPLOYEE_ID)
        .companyAccountId(COMPANY_ID)
        .userEmail(USER_EMAIL)
        .build();
  }

}
