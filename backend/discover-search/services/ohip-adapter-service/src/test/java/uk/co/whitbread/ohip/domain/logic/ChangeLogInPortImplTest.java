package uk.co.whitbread.ohip.domain.logic;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ohip.domain.model.changelog.out.ChangeLogListType;
import uk.co.whitbread.ohip.domain.model.changelog.out.ChangeLogResponse;
import uk.co.whitbread.ohip.domain.model.changelog.out.ChangeLogType;
import uk.co.whitbread.ohip.domain.ports.secondary.ChangeLogOutPort;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChangeLogInPortImplTest {

  @InjectMocks
  private ChangeLogInPortImpl changeLogInPortImpl;
  @Mock
  private ChangeLogOutPort changeLogOutPort;

  @Test
  void getChangeLogShouldReturnOk() {

    when(changeLogOutPort.getChangeLog("HOTEL_ID", "123456", 10, 1))
        .thenReturn(buildMockChangeLogResponse());

    var response = changeLogInPortImpl.getChangeLog("HOTEL_ID", "123456", 10, 1);

    assertNotNull(response);

    verifyNoMoreInteractions(changeLogOutPort);
  }

  private ChangeLogResponse buildMockChangeLogResponse() {
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
