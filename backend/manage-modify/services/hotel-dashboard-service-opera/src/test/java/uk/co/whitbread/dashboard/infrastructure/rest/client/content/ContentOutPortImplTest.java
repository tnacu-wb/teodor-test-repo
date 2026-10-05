package uk.co.whitbread.dashboard.infrastructure.rest.client.content;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.dashboard.domain.model.in.RoomType;
import uk.co.whitbread.dashboard.infrastructure.rest.client.content.mapper.RoomTypeMapper;
import uk.co.whitbread.dashboard.infrastructure.rest.client.content.model.RoomTypeDto;
import uk.co.whitbread.dashboard.infrastructure.rest.client.content.service.ContentClient;

@ExtendWith(MockitoExtension.class)
class ContentOutPortImplTest {

  @InjectMocks
  private ContentOutPortImpl contentOutPort;
  @Mock
  private ContentClient contentClient;
  @Mock
  private RoomTypeMapper roomTypeMapper;

  @Test
  void retrieveBookingDetailsTest() {
    when(contentClient.getRoomTypeInformation(any(), any(), any())).thenReturn(new RoomTypeDto());
    when(roomTypeMapper.toModel(any(RoomTypeDto.class))).thenReturn(new RoomType());

    var response = contentOutPort.getRoomTypes("gb", "en", "pi");

    assertNotNull(response);
    assertThat(response, instanceOf(RoomType.class));
  }

}