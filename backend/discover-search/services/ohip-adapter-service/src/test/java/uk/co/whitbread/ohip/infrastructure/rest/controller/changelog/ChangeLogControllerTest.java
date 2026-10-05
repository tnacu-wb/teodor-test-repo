package uk.co.whitbread.ohip.infrastructure.rest.controller.changelog;

import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ohip.domain.model.changelog.out.ChangeLogListType;
import uk.co.whitbread.ohip.domain.model.changelog.out.ChangeLogResponse;
import uk.co.whitbread.ohip.domain.model.changelog.out.ChangeLogType;
import uk.co.whitbread.ohip.domain.ports.primary.ChangeLogInPort;
import uk.co.whitbread.ohip.infrastructure.rest.controller.changelog.mapper.ChangeLogResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.changelog.model.out.ChangeLogListTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.changelog.model.out.ChangeLogResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.changelog.model.out.ChangeLogTypeDto;

@ExtendWith(MockitoExtension.class)
class ChangeLogControllerTest {

  @InjectMocks
  ChangeLogController changeLogController;
  @Mock
  private ChangeLogInPort changeLogInPort;
  @Mock
  private ChangeLogResponseMapper changeLogResponseMapper;

  @Test
  void getChangeLog_shouldReturnOk() {

    var changeLogResponse = ChangeLogResponse.builder()
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
            .build())
        .build();

    when(changeLogInPort.getChangeLog("HEAPTI", "123456", 10, 1)).thenReturn(changeLogResponse);
    when(changeLogResponseMapper.toDto(changeLogResponse)).thenReturn(createChangeLogResponseDto());

    var response =
        changeLogController.getChangeLog("HEAPTI", "123456", 10, 1);


    Assertions.assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
  }

  private ChangeLogResponseDto createChangeLogResponseDto() {
    return ChangeLogResponseDto.builder()
        .activityLog(ChangeLogListTypeDto.builder()
            .activityLogType(ChangeLogTypeDto.builder()
                .logDate("20/12/2023, 09:43")
                .logUserName("WHBOC001_AWS-MS-SA-CRED927-A@I")
                .actionType("NEW RESERVATION")
                .actionDescription("test description")
                .build())
            .build())
        .activityLog(ChangeLogListTypeDto.builder()
            .activityLogType(ChangeLogTypeDto.builder()
                .logDate("20/12/2023, 09:43")
                .logUserName("WHBOC001_AWS-MS-SA-CRED927-A@I")
                .actionType("UPDATE RESERVATION")
                .actionDescription("test description")
                .build())
            .build())
        .build();
  }
}
