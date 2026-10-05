package uk.co.whitbread.ohip.domain.model.availability.in;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Room {

  private String tag;
  private List<String> roomTypes;
  private Integer adults;
  private Integer children;
  private Integer numberOfRooms;
}
