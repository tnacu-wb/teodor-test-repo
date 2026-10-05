package uk.co.whitbread.domain.model.availability.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeInfo {

  private String roomType;
  private Integer roomNumber;
  private Integer adults;
  private Integer children;
  private Boolean cotRequested;
  @Singular
  private List<Room> rooms;

}
