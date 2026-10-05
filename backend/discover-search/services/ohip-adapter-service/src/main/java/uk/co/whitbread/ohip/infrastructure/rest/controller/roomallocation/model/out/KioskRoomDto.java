package uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class KioskRoomDto {

  private String roomId;
  private HousekeepingDto housekeeping;

}
