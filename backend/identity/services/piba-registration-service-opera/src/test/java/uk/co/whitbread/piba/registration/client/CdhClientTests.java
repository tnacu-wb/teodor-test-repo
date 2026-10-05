package uk.co.whitbread.piba.registration.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.shared.cdh.ApplicationDataService;
import uk.co.whitbread.shared.cdh.RegistrationDataService;
import uk.co.whitbread.shared.cdh.model.GetDashboardDetailsQueryParams;
import uk.co.whitbread.shared.cdh.model.PibaTetheredGuidResponse;
import uk.co.whitbread.shared.cdh.model.applications.UpdateApplicationStatusRequest;
import uk.co.whitbread.shared.cdh.model.spending.application.ApplicationParticipant;
import uk.co.whitbread.shared.cdh.model.spending.application.ApplicationResponse;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CdhClientTests {

  @Mock
  private ApplicationDataService applicationDataService;

  @Mock
  private RegistrationDataService registrationDataService;

  @InjectMocks
  private CdhClient cdhClient;

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
  void getTetheredGuids_WhenInvoked_ThenParamsArePassedCorrectly() {
    var cdhResponse = List.of(PibaTetheredGuidResponse.builder().tetheredGuid("aaa-bbb").build());
    when(registrationDataService.getDashboardDetails(any(), eq("email@email.com"), eq("InnBusiness")))
          .thenReturn(cdhResponse);

    var response = cdhClient.getTetheredGuids("compId", "empId", "email@email.com");

    assertEquals(cdhResponse, response);
    var queryParams = ArgumentCaptor.forClass(GetDashboardDetailsQueryParams.class);
    verify(registrationDataService).getDashboardDetails(queryParams.capture(), eq("email@email.com"), eq("InnBusiness"));
    assertEquals("compId", queryParams.getValue().getCompanyId());
    assertEquals("empId", queryParams.getValue().getEmployeeId());
  }

  @Test
  void updateAppStatus_WhenInvoked_ShouldPassCorrectArguments() {
    // Arrange
    String applicationId = "12345";
    String status = "Completed";
    String userEmail = "user@example.com";

    // Act
    cdhClient.updateAppStatus(applicationId, status, userEmail);

    // Assert
    var request = ArgumentCaptor.forClass(UpdateApplicationStatusRequest.class);
    verify(applicationDataService).updateApplicationStatus(request.capture(), eq(userEmail), eq("InnBusiness"));
    assertEquals(applicationId, request.getValue().getApplicationId());
    assertEquals(status, request.getValue().getStage());
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

}
