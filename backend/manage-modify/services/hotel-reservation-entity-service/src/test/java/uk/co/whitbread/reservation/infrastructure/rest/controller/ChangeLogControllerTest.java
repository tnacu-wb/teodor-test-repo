package uk.co.whitbread.reservation.infrastructure.rest.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.AssertionErrors;
import uk.co.whitbread.reservation.domain.logic.ChangeLogInPortImpl;
import uk.co.whitbread.reservation.domain.model.out.ChangeLogListType;
import uk.co.whitbread.reservation.domain.model.out.ChangeLogResponse;
import uk.co.whitbread.reservation.domain.model.out.ChangeLogType;
import uk.co.whitbread.reservation.infrastructure.rest.controller.changelog.ChangeLogController;
import uk.co.whitbread.reservation.infrastructure.rest.controller.changelog.mapper.ChangeLogResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.changelog.model.out.ChangeLogListTypeDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.changelog.model.out.ChangeLogResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.changelog.model.out.ChangeLogTypeDto;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChangeLogControllerTest {

  @InjectMocks
  private ChangeLogController changeLogController;
  @Mock
  private ChangeLogInPortImpl changeLogInPort;
  @Mock
  private ChangeLogResponseMapper changeLogResponseMapper;
  private static final String HOTELID = "HOTEL_ID";
  private static final String RESERVATIONID = "123456";

  @Test
  void getChangeLog_ShouldReturnOk() {

    var changeLogResponse = ChangeLogResponse.builder()
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
            .build())
        .build();

    when(changeLogInPort.getChangeLog(HOTELID, RESERVATIONID, 10,1 )).thenReturn(changeLogResponse);
    when(changeLogResponseMapper.toDto(changeLogResponse)).thenReturn(createChangeLogResponseDto());

    var response =
        changeLogController.getChangeLog(HOTELID, RESERVATIONID, 10, 1);

    assertNotNull(response);
    AssertionErrors.assertEquals(response.toString(), 200, response.getStatusCode().value());
  }

  private ChangeLogResponseDto createChangeLogResponseDto() {
    return ChangeLogResponseDto.builder()
        .activityLog(ChangeLogListTypeDto.builder()
            .activityLogType(ChangeLogTypeDto.builder()
                .date("20/12/2023")
                .time("09:43")
                .user("WHBOC001_AWS-MS-SA-CRED927-A@I")
                .actionType("NEW RESERVATION")
                .actionDescription("test description")
                .build())
            .build())
        .activityLog(ChangeLogListTypeDto.builder()
            .activityLogType(ChangeLogTypeDto.builder()
                .date("20/12/2023")
                .time("09:43")
                .user("WHBOC001_AWS-MS-SA-CRED927-A@I")
                .actionType("UPDATE RESERVATION")
                .actionDescription("test description")
                .build())
            .build())
        .build();
  }
}
