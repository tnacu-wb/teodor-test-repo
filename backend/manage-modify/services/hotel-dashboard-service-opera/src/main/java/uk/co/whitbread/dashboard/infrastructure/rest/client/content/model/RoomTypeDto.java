package uk.co.whitbread.dashboard.infrastructure.rest.client.content.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeDto {

  private List<RoomTypeInformationDto> roomTypes;

}
