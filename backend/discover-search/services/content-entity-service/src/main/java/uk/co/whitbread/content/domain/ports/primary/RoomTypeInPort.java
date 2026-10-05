package uk.co.whitbread.content.domain.ports.primary;

import uk.co.whitbread.content.domain.model.roomtype.in.RoomTypeRequest;
import uk.co.whitbread.content.domain.model.roomtype.out.RoomType;

public interface RoomTypeInPort {

  RoomType getRoomType(RoomTypeRequest roomTypeRequest);
}
