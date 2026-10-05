package uk.co.whitbread.domain.model.availability.in;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Room {

  private String tag;
  private Integer adults;
  private Integer children;
  private Integer numberOfRooms;
  private String roomType;
}
