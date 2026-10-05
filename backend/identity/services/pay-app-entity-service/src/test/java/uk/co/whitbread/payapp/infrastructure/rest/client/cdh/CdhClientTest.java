package uk.co.whitbread.payapp.infrastructure.rest.client.cdh;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import uk.co.whitbread.payapp.ErrorCode;
import uk.co.whitbread.payapp.domain.model.in.Scheme;
import uk.co.whitbread.payapp.domain.model.in.jwt.JwtTokenClaims;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.exceptions.CdhResponseException;
import uk.co.whitbread.payapp.infrastructure.security.JwtUtils;
import uk.co.whitbread.shared.cdh.ApplicationDataService;
import uk.co.whitbread.shared.cdh.RegistrationDataService;
import uk.co.whitbread.shared.cdh.model.Participants;
import uk.co.whitbread.shared.cdh.model.PibaTetheredGuidResponse;
import uk.co.whitbread.shared.cdh.model.StartApplicationRequest;
import uk.co.whitbread.shared.cdh.model.StartApplicationResponse;
import uk.co.whitbread.shared.cdh.model.UpdateApplicationRequest;
import uk.co.whitbread.shared.cdh.model.UpdateApplicationResponse;
import uk.co.whitbread.shared.cdh.model.applications.UpdateApplicationStatusRequest;
import uk.co.whitbread.shared.cdh.model.applications.UpdateApplicationStatusResponse;
import uk.co.whitbread.shared.cdh.model.spending.application.ApplicationParticipant;
import uk.co.whitbread.shared.cdh.model.spending.application.ApplicationResponse;
import uk.co.whitbread.shared.cdh.model.spending.application.CardHolder;
import uk.co.whitbread.shared.cdh.model.spending.application.UpdateApplicationCardHoldersRequest;

@ExtendWith(MockitoExtension.class)
class CdhClientTest {

  @InjectMocks
  private CdhClient cdhClient;

  @Mock
  private ApplicationDataService applicationDataService;

  @Mock
  RegistrationDataService registrationDataService;

  @Mock
  private CacheManager cacheManager1Hour;

  @Mock
  private JwtUtils jwtUtils;

  @Test
  void startApplication__ShouldReturnOK() {
    // Arrange
    when(applicationDataService.startApplication(any(), any(), any())).thenReturn(createStartApplicationResponse());

    // Act
    var startApplicationResponse = cdhClient.startApplication(createStartApplicationRequest());

    // Assert
    assertThat(startApplicationResponse, notNullValue());
    assertEquals(200, startApplicationResponse.getStatus());
  }

  @Test
  void fetchApplicationsByUser__ShouldReturnOK() {
    // Arrange
    when(applicationDataService.fetchApplicationsByUser(any(), any(), any(), any())).thenReturn(
        List.of(createApplicationResponse()));
    var jwtTokenClaims = new JwtTokenClaims("companyId", "participantId", "email");
    // Act
    var fetchApplicationsByUser = cdhClient.fetchApplicationsByUser(jwtTokenClaims);

    // Assert
    assertThat(fetchApplicationsByUser, notNullValue());
    assertEquals(1, fetchApplicationsByUser.size());
    assertEquals("6609", fetchApplicationsByUser.get(0).getApplicationId());
  }

  @Test
  void fetchApplication__ShouldReturnOK() {
    // Arrange
    when(applicationDataService.fetchApplication(any(), any(), any(), any())).thenReturn(
        List.of(createApplicationResponse()));

    // Act
    var application = cdhClient
        .fetchApplication("6609", "12u7gjk-9118-4683-8286-771b90169f05", "email");

    // Assert
    assertThat(application, notNullValue());
    assertEquals("6609", application.getApplicationId());
  }

  @Test
  void fetchApplication__ShouldReturnNull() {
    // Arrange
    when(applicationDataService.fetchApplication(any(), any(), any(), any())).thenReturn(List.of());

    // Act
    var application = cdhClient
        .fetchApplication("6609", "12u7gjk-9118-4683-8286-771b90169f05", "email");

    // Assert
    assertThat(application, nullValue());
  }

  @Test
  void updateApplicationStatus_ShouldReturnResponse_WhenSuccessful() {
    // Arrange
    UpdateApplicationStatusRequest request = new UpdateApplicationStatusRequest("appId", "Cancelled");
    UpdateApplicationStatusResponse expectedResponse = new UpdateApplicationStatusResponse(204, "Success");
    when(applicationDataService.updateApplicationStatus(any(UpdateApplicationStatusRequest.class), any(String.class), any(String.class)))
        .thenReturn(expectedResponse);
    when(jwtUtils.parseToken()).thenReturn(new JwtTokenClaims("companyId", "participantId", "email"));
    Cache cache = mock(Cache.class);
    when(cacheManager1Hour.getCache("ListApplicationsCache")).thenReturn(cache);

    // Act
    UpdateApplicationStatusResponse actualResponse = cdhClient.updateApplicationStatus(request, "test@example.com");

    // Assert
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void updateApplicationStatus_ShouldThrowException_WhenServiceFails() {
    // Arrange
    UpdateApplicationStatusRequest request = new UpdateApplicationStatusRequest("appId", "Cancelled");
    when(applicationDataService.updateApplicationStatus(any(UpdateApplicationStatusRequest.class), any(String.class), any(String.class)))
        .thenThrow(new RuntimeException("Service error"));
    when(jwtUtils.parseToken()).thenReturn(new JwtTokenClaims("companyId", "participantId", "email"));
    Cache cache = mock(Cache.class);
    when(cacheManager1Hour.getCache("ListApplicationsCache")).thenReturn(cache);

    // Act & Assert
    CdhResponseException exception = assertThrows(CdhResponseException.class, () ->
        cdhClient.updateApplicationStatus(request, "test@example.com"));
    assertEquals(ErrorCode.CDH_APP_CANCEL_ERROR.getCode(), exception.getErrorCode());
    assertEquals("Failed to update application status in CDH for applicationId = appId", exception.getMessage());
  }

  @Test
  void updateApplication__ShouldReturnOK() {
    // Arrange
    when(applicationDataService.updateApplication(any(), any(), any())).thenReturn(createUpdateApplicationResponse());
    when(jwtUtils.parseToken()).thenReturn(new JwtTokenClaims("companyId", "participantId", "email"));
    Cache cache = mock(Cache.class);
    when(cacheManager1Hour.getCache("ListApplicationsCache")).thenReturn(cache);

    // Act
    var updateApplicationResponse = cdhClient.updateApplication(createUpdateApplicationRequest(), "accessedBy");

    // Assert
    assertThat(updateApplicationResponse, notNullValue());
    assertEquals(200, updateApplicationResponse.getStatus());
  }

  @ParameterizedTest
  @CsvSource({
      "'3213231', '32131', 'email@yopmail.com'",   // all non-null
      "'3213231', '32131', 'null'",                // email null
      "'3213231', 'null', 'email@yopmail.com'",    // employeeId null
      "'3213231', 'null', 'null'",                 // employeeId & email null
      "'null', '32131', 'email@yopmail.com'",      // companyId null
      "'null', '32131', 'null'",                   // companyId & email null
      "'null', 'null', 'email@yopmail.com'",       // companyId & employeeId null
      "'null', 'null', 'null'"                     // all null
  })
  void updateApplication__InvalidDataCacheEvictShouldReturnOK(String companyId, String employeeId, String email) {
    // Arrange
    when(applicationDataService.updateApplication(any(), any(), any())).thenReturn(createUpdateApplicationResponse());
    when(jwtUtils.parseToken()).thenReturn(new JwtTokenClaims(companyId, employeeId, email));
    Cache cache = mock(Cache.class);
    when(cacheManager1Hour.getCache("ListApplicationsCache")).thenReturn(cache);

    // Act
    var updateApplicationResponse = cdhClient.updateApplication(createUpdateApplicationRequest(), "accessedBy");

    // Assert
    assertThat(updateApplicationResponse, notNullValue());
    assertEquals(200, updateApplicationResponse.getStatus());
  }

  @Test
  void updateApplication__InvalidCacheEvictShouldReturnOK() {
    // Arrange
    when(applicationDataService.updateApplication(any(), any(), any())).thenReturn(createUpdateApplicationResponse());
    when(jwtUtils.parseToken()).thenReturn(new JwtTokenClaims("companyId", "participantId", "email"));
    when(cacheManager1Hour.getCache("ListApplicationsCache")).thenReturn(null);

    // Act
    var updateApplicationResponse = cdhClient.updateApplication(createUpdateApplicationRequest(), "accessedBy");

    // Assert
    assertThat(updateApplicationResponse, notNullValue());
    assertEquals(200, updateApplicationResponse.getStatus());
  }

  @Test
  void updateApplication__InvalidTokenShouldReturnOK() {
    // Arrange
    when(applicationDataService.updateApplication(any(), any(), any())).thenReturn(createUpdateApplicationResponse());
    when(jwtUtils.parseToken()).thenReturn(null);
    Cache cache = mock(Cache.class);
    when(cacheManager1Hour.getCache("ListApplicationsCache")).thenReturn(cache);

    // Act
    var updateApplicationResponse = cdhClient.updateApplication(createUpdateApplicationRequest(), "accessedBy");

    // Assert
    assertThat(updateApplicationResponse, notNullValue());
    assertEquals(200, updateApplicationResponse.getStatus());
  }


  @Test
  void updateApplicationCardHolders__ShouldCallServiceAndEvictCache() {

    // Arrange
    var request = new UpdateApplicationCardHoldersRequest("appId",
        List.of(CardHolder.builder().employeeId(231321).build()));
    String email = "test@example.com";
    JwtTokenClaims claims = new JwtTokenClaims("companyId", "participantId", email);
    when(jwtUtils.parseToken()).thenReturn(claims);
    Cache cache = mock(Cache.class);
    when(cacheManager1Hour.getCache("ListApplicationsCache")).thenReturn(cache);

    // Act & Assert
    assertDoesNotThrow(() -> cdhClient.updateApplicationCardHolders(request, email));
    verify(applicationDataService).updateApplicationCardHolders(request, email, "InnBusiness");
    verify(cache).evict("companyId:participantId:" + email);
  }

  @Test
  void getTetheredGuids__ShouldReturnOK() {
    // Arrange
    var companyId = 123;
    var employeeId = 321;
    var tetheredGuid = "tetheredGuid";
    when(registrationDataService.getDashboardDetails(any(), any(), any())).thenReturn(List.of(
        PibaTetheredGuidResponse.builder().companyId(companyId).employeeId(employeeId)
            .tetheredGuid(tetheredGuid).build()));

    // Act
    var tetheredGuids = cdhClient
        .getTetheredGuids(String.valueOf(companyId), String.valueOf(employeeId), "email");

    // Assert
    assertThat(tetheredGuids, notNullValue());
    assertThat(tetheredGuids, hasSize(1));

    var pibaTetheredGuidResponse = tetheredGuids.get(0);
    assertEquals(tetheredGuid, pibaTetheredGuidResponse.getTetheredGuid());
    assertEquals(companyId, pibaTetheredGuidResponse.getCompanyId());
    assertEquals(employeeId, pibaTetheredGuidResponse.getEmployeeId());
  }

  StartApplicationResponse createStartApplicationResponse() {
    return StartApplicationResponse.builder()
        .status(200)
        .message("Application saved Successfully.")
        .build();
  }

  StartApplicationRequest createStartApplicationRequest() {
    return StartApplicationRequest.builder()
        .applicationId("123")
        .applicationNumber("123")
        .applicationGuid("123")
        .startedDate("2025-02-25T11:07:57.975601Z")
        .companyId(1)
        .accountName("TemporaryAccountName")
        .scheme(Scheme.DE.toString())
        .stage("Incomplete Application")
        .participants(Participants.builder()
            .initiator(true)
            .participantId(1)
            .delegated(false)
            .terms(false)
            .directdebit(false)
            .email("john.doe@email.com")
            .shared("2025-02-25T11:07:57.975601Z")
            .name("Temporary Name")
            .build())
        .build();
  }

  ApplicationResponse createApplicationResponse() {
    var participant = ApplicationParticipant.builder()
        .initiator(true)
        .participantId(897)
        .delegated(false)
        .terms(true)
        .email("Eric1.doenew@example.com")
        .shared("2024-09-04T10:00:00Z")
        .name("Eric1 Doenew")
        .build();

    return ApplicationResponse.builder()
        .applicationId("6609")
        .applicationNumber("667")
        .applicationGuid("12u7gjk-9118-4683-8286-771b90169f05")
        .companyId(7878)
        .startedDate("2024-09-04T10:00:00Z")
        .scheme("Standard")
        .updateDate("0001-01-01T00:00:00Z")
        .accountName("Poorvi")
        .stage("Accepted")
        .resumeUrl(null)
        .submittedDate("0001-01-01T00:00:00Z")
        .activatedDate("0001-01-01T00:00:00Z")
        .participants(Collections.singletonList(participant))
        .cardHolders(null)
        .created("2024-07-10T07:54:02.3205314Z")
        .modified("0001-01-01T00:00:00Z")
        .build();
  }

  UpdateApplicationResponse createUpdateApplicationResponse() {
    return UpdateApplicationResponse.builder()
        .status(200)
        .message("Application updated Successfully.")
        .build();
  }

  UpdateApplicationRequest createUpdateApplicationRequest() {
    return UpdateApplicationRequest.builder()
        .applicationId("123")
        .resumeUrl("http://resume.url")
        .updateDate("2025-02-25T11:07:57.975601Z")
        .stage("Incomplete Application")
        .participants(List.of(createParticipant()))
        .build();
  }

  Participants createParticipant() {
    return Participants.builder()
        .initiator(true)
        .participantId(1)
        .delegated(false)
        .terms(false)
        .directdebit(false)
        .email("john.doe@email.com")
        .shared("2025-02-25T11:07:57.975601Z")
        .name("John Doe")
        .build();
  }

}
