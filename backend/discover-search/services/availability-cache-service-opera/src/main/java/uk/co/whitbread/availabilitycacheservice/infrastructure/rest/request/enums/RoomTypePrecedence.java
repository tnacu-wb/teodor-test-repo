package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.enums;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoomTypePrecedence {
  SB(1, 0),
  DB(2, 0),
  FAM(2, 1),
  TWIN(2, 0),
  DIS(1, 0);

  private final int adults;
  private final int children;

  public List<Integer> getOccupancy() {
    return List.of(adults, children);
  }
}
