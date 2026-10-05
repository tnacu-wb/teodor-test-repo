package uk.co.whitbread.reservation.domain.logic;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.reservation.domain.model.out.ChangeLogListType;
import uk.co.whitbread.reservation.domain.model.out.ChangeLogResponse;
import uk.co.whitbread.reservation.domain.model.out.ChangeLogType;
import uk.co.whitbread.reservation.domain.ports.secondary.ChangeLogOutPort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChangeLogInPortImpTest {

  @InjectMocks
  private ChangeLogInPortImpl changeLogInPortImpl;
  @Mock
  private ChangeLogOutPort changeLogOutPort;
  private static final String HOTELID = "HOTEL_ID";
  private static final String RESERVATIONID = "123456";
  private static final Integer LIMIT = 10;
  private static final Integer OFFSET = 5;

  @Test
  void getChangeLog_Success() {

    when(changeLogOutPort.getChangeLog(HOTELID, RESERVATIONID, LIMIT, OFFSET)).thenReturn(mockChangeLogResponse());

    var actual = changeLogInPortImpl.getChangeLog(HOTELID, RESERVATIONID, LIMIT, OFFSET);

    assertNotNull(actual);
    assertEquals(2, actual.getActivityLog().getTotalResults());
  }

  private ChangeLogResponse mockChangeLogResponse() {

    return ChangeLogResponse.builder()
        .activityLog(ChangeLogListType.builder()
            .activityLogType(ChangeLogType.builder()
                .date("20/12/2023")
                .time("09:43")
                .user("WHBOC001_AWS-MS-SA-CRED927-A@I")
                .actionType("NEW RESERVATION")
                .actionDescription("test description")
                .build())
            .activityLogType(ChangeLogType.builder()
                .date("20/12/2023")
                .time("09:43")
                .user("WHBOC001_AWS-MS-SA-CRED927-A@I")
                .actionType("UPDATE RESERVATION")
                .actionDescription("test description")
                .build())
            .totalResults(2)
            .build())
        .build();
  }

}
