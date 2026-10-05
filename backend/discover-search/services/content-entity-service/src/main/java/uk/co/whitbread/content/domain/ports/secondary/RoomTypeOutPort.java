package uk.co.whitbread.content.domain.ports.secondary;

import uk.co.whitbread.content.domain.model.roomtype.in.RoomTypeRequest;
import uk.co.whitbread.content.domain.model.roomtype.out.RoomType;

public interface RoomTypeOutPort {

  RoomType getRoomType(RoomTypeRequest request);
}
