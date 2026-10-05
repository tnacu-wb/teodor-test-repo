package uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaxRoomOccupancyDataDto {

  Integer adultsNumber;
  Integer childrenNumber;
  List<String> acceptedRoomTypes;

}
