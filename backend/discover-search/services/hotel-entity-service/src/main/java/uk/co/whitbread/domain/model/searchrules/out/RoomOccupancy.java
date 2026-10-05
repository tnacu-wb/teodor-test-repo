package uk.co.whitbread.domain.model.searchrules.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoomOccupancy {

  private List<String> acceptedRoomTypes = null;
  private Integer adultsNumber;
  private Integer childrenNumber;
}
