package uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaxRoomOccupancyResponseDto {

  private String channelId;
  private String brand;
  private List<MaxRoomOccupancyDataDto> roomOccupancies;
  LocalDateTime generatedAt;

}
