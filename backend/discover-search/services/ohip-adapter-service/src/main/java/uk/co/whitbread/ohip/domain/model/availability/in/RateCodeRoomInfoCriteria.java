package uk.co.whitbread.ohip.domain.model.availability.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode()
public class RateCodeRoomInfoCriteria {

  @NotEmpty
  private String roomType;
  @NotNull
  @Positive
  private Integer adultsNo;
  @PositiveOrZero
  private Integer childrenNo;

  public RateCodeRoomInfoCriteria(String roomType, Integer adultsNo, Integer childrenNo) {
    this.roomType = roomType;
    this.adultsNo = adultsNo;
    this.childrenNo = childrenNo;
  }
}
