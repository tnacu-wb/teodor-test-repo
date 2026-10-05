package uk.co.whitbread.rules.agent.domain.model.out;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.rules.agent.domain.model.validation.ModelValidator;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class MaxRoomOccupancyData extends ModelValidator<MaxRoomOccupancyData> {

  @NotNull
  Integer adultsNumber;
  @NotNull
  Integer childrenNumber;
  @NotNull
  List<String> acceptedRoomTypes;

  public MaxRoomOccupancyData(Integer adults, Integer children,
      List<String> acceptedRoomTypes) {
    this.adultsNumber = adults;
    this.childrenNumber = children;
    this.acceptedRoomTypes = acceptedRoomTypes;
    this.validateSelf();
  }
}
