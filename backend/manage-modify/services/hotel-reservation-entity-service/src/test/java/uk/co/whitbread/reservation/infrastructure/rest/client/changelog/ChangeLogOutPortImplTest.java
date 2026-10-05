package uk.co.whitbread.reservation.infrastructure.rest.client.changelog;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeLogResponseDto;
import uk.co.whitbread.reservation.domain.model.out.ChangeLogListType;
import uk.co.whitbread.reservation.domain.model.out.ChangeLogResponse;
import uk.co.whitbread.reservation.domain.model.out.ChangeLogType;
import uk.co.whitbread.reservation.infrastructure.rest.client.changelog.mapper.ChangeLogResponseOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service.OhipAdapterClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChangeLogOutPortImplTest {

  @InjectMocks
  private ChangeLogOutPortImpl changeLogOutPortImpl;
  @Mock
  private OhipAdapterClient ohipAdapterClient;
  @Mock
  private ChangeLogResponseOhipMapper changeLogResponseOhipMapper;
  private static final String HOTELID = "HOTEL_ID";
  private static final String RESERVATIONID = "123456";
  private static final ObjectMapper mapper = new ObjectMapper();
  private static final Integer LIMIT = 10;
  private static final Integer OFFSET = 5;

  @Test
  void getChangeLog_Success() throws IOException {

    var changeLogResponseDto = mapper.readValue(ChangeLogOutPortImplTest.class.getClassLoader()
        .getResource("__files/get_change_log_success_response.json"), ChangeLogResponseDto.class);

    when(ohipAdapterClient.getChangeLog(HOTELID, RESERVATIONID, LIMIT, OFFSET)).thenReturn(changeLogResponseDto);
    when(changeLogResponseOhipMapper.toModel(changeLogResponseDto)).thenReturn(mockChangeLogResponse());

    var actual = changeLogOutPortImpl.getChangeLog(HOTELID, RESERVATIONID, LIMIT, OFFSET);

    assertNotNull(actual);
    assertEquals(2, actual.getActivityLog().getActivityLog().size());
  }

  private ChangeLogResponse mockChangeLogResponse() {

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
            .totalResults(2)
            .build())
        .build();
  }

}
