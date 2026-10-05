package uk.co.whitbread.shared.cdh;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import uk.co.whitbread.shared.cdh.model.Participants;
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
import uk.co.whitbread.shared.cdh.properties.CdhApiOauthProperties;
import uk.co.whitbread.shared.cdh.properties.CdhApiProperties;

@ExtendWith(MockitoExtension.class)
class ApplicationDataServiceTest {

  private static final String ACCESSED_BY = "customer@mail.com";
  private static final String ACCESS_CONTEXT = "InBusiness";
  private static final String HOST = "https://localhost";
  private static final String SUBSCRIPTION_KEY = "subscription-key";

  @Mock
  private CdhApiProperties cdhApiProperties;
  @Mock
  private CdhApiOauthProperties cdhApiOauthProperties;
  @Mock
  private CustomerDataHubClient cdhClient;

  @InjectMocks
  private ApplicationDataService applicationDataService;

  @BeforeEach
  void setup() {
    when(cdhApiProperties.getHost()).thenReturn(HOST);
    when(cdhApiOauthProperties.getSubscriptionKey()).thenReturn(SUBSCRIPTION_KEY);
  }

  @Test
  void startApplication_success() {

    Participants participants = Participants.builder()
        .initiator(false)
        .participantId(89)
        .delegated(true)
        .terms(true)
        .directdebit(false)
        .email("email@google.com")
        .shared("2024-09-04T10:00:00Z")
        .name("Name")
        .build();
    StartApplicationRequest request = StartApplicationRequest.builder()
        .applicationId("992")
        .applicationNumber("30")
        .applicationGuid("12u7gjk-9118-4683-8286-771b90169f054")
        .startedDate("2024-09-04T10:00:00Z")
        .companyId(9)
        .accountName("Testnew1")
        .scheme("Standard")
        .stage("Outstanding")
        .participants(participants)
        .build();

    when(cdhClient.postCDH(anyString(),
        any(StartApplicationRequest.class),
        any(HttpHeaders.class),
        eq(StartApplicationRequest.class),
        eq(StartApplicationResponse.class)))
        .thenReturn(buildStartApplicationResponse());

    var response = applicationDataService.startApplication(request, ACCESSED_BY, ACCESS_CONTEXT);

    assertEquals(200, response.getStatus());
    assertNotNull(response.getMessage());
  }

  @Test
  void updateApplication_success() {

    Participants participants = Participants.builder()
        .initiator(false)
        .participantId(89)
        .delegated(true)
        .terms(true)
        .directdebit(false)
        .email("email@google.com")
        .shared("2024-09-04T10:00:00Z")
        .name("Name")
        .build();
    UpdateApplicationRequest request = UpdateApplicationRequest.builder()
        .applicationId("992")
        .stage("Outstanding")
        .hostedPageGuid("12u7gjk-9118-4683-8286-771b90169f054")
        .directDebitOption("DIRECT")
        .participants(List.of(participants))
        .build();

    when(cdhClient.postCDH(anyString(),
        any(UpdateApplicationRequest.class),
        any(HttpHeaders.class),
        eq(UpdateApplicationRequest.class),
        eq(UpdateApplicationResponse.class)))
        .thenReturn(buildUpdateApplicationResponse());

    var response = applicationDataService.updateApplication(request, ACCESSED_BY, ACCESS_CONTEXT);

    assertEquals(200, response.getStatus());
    assertNotNull(response.getMessage());
  }

  @Test
  void fetchApplicationsByUser_success() {
    int companyId = 1234;
    int participantId = 567;

    when(cdhClient.getListCDH(anyString(),
        any(HttpHeaders.class),
        eq(ApplicationResponse.class)))
        .thenReturn(buildFetchApplicationResponse(companyId, participantId));

    var responseList = applicationDataService.fetchApplicationsByUser(String.valueOf(companyId),
        String.valueOf(participantId), ACCESSED_BY, ACCESS_CONTEXT);

    assertEquals(1, responseList.size());
    assertEquals(companyId, responseList.get(0).getCompanyId());
    assertEquals(1, responseList.get(0).getParticipants().size());
    assertEquals(participantId, responseList.get(0).getParticipants().get(0).getParticipantId());
    assertEquals("DIRECT", responseList.get(0).getDirectDebitOption());
  }

  @Test
  void fetchApplication_success() {
    var companyId = 1234;
    var participantId = 567;

    var applicationIdId = "applicationId";
    var applicationGuid = "applicationGuid";

    when(cdhClient.getListCDH(anyString(),
        any(HttpHeaders.class),
        eq(ApplicationResponse.class)))
        .thenReturn(buildFetchApplicationResponse(companyId, participantId));

    var responseList = applicationDataService
        .fetchApplication(applicationIdId, applicationGuid, ACCESSED_BY, ACCESS_CONTEXT);
    assertEquals(1, responseList.size());

    var response = responseList.get(0);
    var participants = response.getParticipants();

    assertEquals(companyId, response.getCompanyId());
    assertEquals(1, participants.size());
    assertEquals(participantId, participants.get(0).getParticipantId());
  }

  @Test
  void updateApplicationStatus_success() {
    // Arrange
    UpdateApplicationStatusRequest request = new UpdateApplicationStatusRequest();
    UpdateApplicationStatusResponse expectedResponse = UpdateApplicationStatusResponse.builder()
        .status(200)
        .message("Success").build();

    when(cdhClient.postCDH(any(String.class), eq(request), any(),
        eq(UpdateApplicationStatusRequest.class), eq(UpdateApplicationStatusResponse.class)))
        .thenReturn(expectedResponse);

    // Act
    UpdateApplicationStatusResponse actualResponse = applicationDataService.updateApplicationStatus(
        request, ACCESSED_BY, ACCESS_CONTEXT);

    // Assert
    assertNotNull(actualResponse);
    assertEquals(expectedResponse.getStatus(), actualResponse.getStatus());
    assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
  }

  @Test
  void updateApplicationCardHolders_success() {
    // Arrange
    UpdateApplicationCardHoldersRequest request = UpdateApplicationCardHoldersRequest.builder()
        .applicationId("12u7gjk-9118-4683-8286-771b90169f05")
        .cardHolders(List.of(
            CardHolder.builder()
                .employeeId(456)
                .userGuid("101ddece-0c63-4b0d-aca7-9350a159143d")
                .build()
        ))
        .build();

    when(cdhClient.postCDH(anyString(),
        eq(request),
        any(HttpHeaders.class),
        eq(UpdateApplicationCardHoldersRequest.class),
        eq(Void.class)))
        .thenReturn(null);

    // Act
    var response = applicationDataService.updateApplicationCardHolders(request, ACCESSED_BY,
        ACCESS_CONTEXT);

    // Assert
    assertThat(response, nullValue());
  }

  private List<ApplicationResponse> buildFetchApplicationResponse(int companyId,
      int participantId) {
    return List.of(ApplicationResponse.builder()
        .companyId(companyId)
        .participants(List.of(ApplicationParticipant.builder()
            .participantId(participantId)
            .build()))
        .directDebitOption("DIRECT")
        .hostedPageGuid("12u7gjk-9118-4683-8286-771b90169f054")
        .build());
  }

  private StartApplicationResponse buildStartApplicationResponse() {
    return StartApplicationResponse.builder()
        .status(200)
        .message("An application with ApplicationId already exists.").build();
  }

  private UpdateApplicationResponse buildUpdateApplicationResponse() {
    return UpdateApplicationResponse.builder()
        .status(200)
        .message("An application with ApplicationId already exists.").build();
  }
}
