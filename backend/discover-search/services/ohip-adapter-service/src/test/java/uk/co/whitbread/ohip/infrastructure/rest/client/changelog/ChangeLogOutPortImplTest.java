package uk.co.whitbread.ohip.infrastructure.rest.client.changelog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ActivityLog;
import uk.co.whitbread.ohip.domain.model.changelog.out.ChangeLogListType;
import uk.co.whitbread.ohip.domain.model.changelog.out.ChangeLogResponse;
import uk.co.whitbread.ohip.domain.model.changelog.out.ChangeLogType;
import uk.co.whitbread.ohip.infrastructure.rest.client.changelog.mapper.ChangeLogResponseOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.ohip.OhipReservationClient;

@ExtendWith(MockitoExtension.class)
class ChangeLogOutPortImplTest {

  @InjectMocks
  private ChangeLogOutPortImpl changeLogOutPortImpl;
  @Mock
  private OhipReservationClient ohipReservationClient;
  @Mock
  private ChangeLogResponseOhipMapper changeLogResponseOhipMapper;
  private static final ObjectMapper mapper = new ObjectMapper()
      .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
  @Test
  void getChangeLogShouldReturnOk() throws IOException {

    var activityLog = mapper.readValue(ChangeLogOutPortImplTest.class.getClassLoader()
        .getResource("__files/get_activity_log_success_response.json"), ActivityLog.class);

    when(ohipReservationClient.getActivityLog("HOTEL_ID", "123456", 10, 1))
        .thenReturn(activityLog);
    when(changeLogResponseOhipMapper.toDomainModel(activityLog)).thenReturn(buildChangeLogResponse());

    var response = changeLogOutPortImpl.getChangeLog("HOTEL_ID", "123456",
        10, 1);

    assertNotNull(response);
  }

  @Test
  void verifyGetChangeLog_ReturnsResultsBasedOnLimitAndOffset() throws IOException {
    var activityLog = mapper.readValue(ChangeLogOutPortImplTest.class.getClassLoader()
        .getResource("__files/get_activity_log_success_response.json"), ActivityLog.class);

    when(ohipReservationClient.getActivityLog("HOTEL_ID", "123456", 3, 0))
        .thenReturn(activityLog);
    when(changeLogResponseOhipMapper.toDomainModel(activityLog)).thenReturn(buildChangeLogResponse());

    var response = changeLogOutPortImpl.getChangeLog("HOTEL_ID", "123456",
        3, 0);

    assertNotNull(response);
    assertEquals(true, response.getActivityLog().getHasMore());
    assertEquals(Integer.valueOf(2), response.getActivityLog().getTotalPages());
  }

  private ChangeLogResponse buildChangeLogResponse() {

    return ChangeLogResponse.builder()
        .activityLog(ChangeLogListType.builder()
            .activityLogType(ChangeLogType.builder()
                .logDate("20/12/2023, 09:43")
                .logUserName("WHBOC001_AWS-MS-SA-CRED927-A@I")
                .actionType("NEW RESERVATION")
                .actionDescription("test description")
                .build())
            .activityLogType(ChangeLogType.builder()
                .logDate("20/12/2023, 09:43")
                .logUserName("WHBOC001_AWS-MS-SA-CRED927-A@I")
                .actionType("UPDATE RESERVATION")
                .actionDescription("test description")
                .build())
            .activityLogType(ChangeLogType.builder()
                .logDate("20/12/2023, 09:43")
                .logUserName("WHBOC001_AWS-MS-SA-CRED927-A@I")
                .actionType("UPDATE RESERVATION")
                .actionDescription("test description")
                .build())
            .activityLogType(ChangeLogType.builder()
                .logDate("20/12/2023, 09:43")
                .logUserName("WHBOC001_AWS-MS-SA-CRED927-A@I")
                .actionType("UPDATE RESERVATION")
                .actionDescription("test description")
                .build())
            .activityLogType(ChangeLogType.builder()
                .logDate("20/12/2023, 09:43")
                .logUserName("WHBOC001_AWS-MS-SA-CRED927-A@I")
                .actionType("UPDATE RESERVATION")
                .actionDescription("test description")
                .build())
            .totalResults(5)
            .limit(5)
            .offset(5)
            .hasMore(true)
            .totalPages(2)
            .build())
        .build();
  }

}
