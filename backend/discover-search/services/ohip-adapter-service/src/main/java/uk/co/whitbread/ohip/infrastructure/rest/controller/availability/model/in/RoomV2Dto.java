package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

@Data
public class RoomV2Dto {

  @NotEmpty
  private String tag;
  private List<String> roomTypes;
  private Integer adults;
  private Integer children;
  private Integer numberOfRooms;
}
