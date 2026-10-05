package uk.co.whitbread.dashboard.infrastructure.rest.client.content;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.co.whitbread.dashboard.domain.model.in.RoomType;
import uk.co.whitbread.dashboard.domain.ports.secondary.ContentOutPort;
import uk.co.whitbread.dashboard.infrastructure.rest.client.content.mapper.RoomTypeMapper;
import uk.co.whitbread.dashboard.infrastructure.rest.client.content.service.ContentClient;

@Component
@RequiredArgsConstructor
public class ContentOutPortImpl implements ContentOutPort {

  private final RoomTypeMapper roomTypeMapper;
  private final ContentClient contentClient;

  @Override
  public RoomType getRoomTypes(String country, String language, String brand) {
    return roomTypeMapper.toModel(contentClient.getRoomTypeInformation(country, language, brand));
  }
}
