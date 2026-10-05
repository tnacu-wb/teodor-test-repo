package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out;

import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor

public class SessionResponseDto {

  List<SessionDatesDto> dates;

  public SessionResponseDto(List<SessionDatesDto> dates) {
    this.dates = dates;
  }
}
