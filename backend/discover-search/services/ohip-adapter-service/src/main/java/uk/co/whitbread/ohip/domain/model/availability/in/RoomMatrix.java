package uk.co.whitbread.ohip.domain.model.availability.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomMatrix {

  Integer quantity;
  List<String> originalRoomType;
  List<Integer> adults;
  List<Integer> children;
  List<Boolean> cotRequired;
  List<String> roomsSubstitutionList;

}
