package uk.co.whitbread.content.domain.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.content.domain.model.roomtype.in.RoomTypeRequest;
import uk.co.whitbread.content.domain.model.roomtype.out.RoomType;
import uk.co.whitbread.content.domain.ports.primary.RoomTypeInPort;
import uk.co.whitbread.content.domain.ports.secondary.RoomTypeOutPort;

@Slf4j
@RequiredArgsConstructor
public class RoomTypeInPortImpl implements RoomTypeInPort {

  private final RoomTypeOutPort roomTypeOutPort;

  @Override
  public RoomType getRoomType(RoomTypeRequest roomTypeRequest) {
    return roomTypeOutPort.getRoomType(roomTypeRequest);
  }
}
