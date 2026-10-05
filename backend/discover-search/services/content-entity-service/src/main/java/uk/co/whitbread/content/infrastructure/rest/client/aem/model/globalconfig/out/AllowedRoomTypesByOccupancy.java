package uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllowedRoomTypesByOccupancy {

  private List<String> acceptedRoomTypes;
  private Integer adultsNumber;
  private Integer childrenNumber;
}
