package uk.co.whitbread.reservation.domain.model.availability.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Room {

  private String tag;
  private Integer adults;
  private Integer children;
  private Integer numberOfRooms;
  private String roomType;
}
