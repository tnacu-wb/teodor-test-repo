package uk.co.whitbread.dashboard.domain.ports.secondary;

import uk.co.whitbread.dashboard.domain.model.in.RoomType;

public interface ContentOutPort {

  RoomType getRoomTypes(String country, String language, String brand);
}
