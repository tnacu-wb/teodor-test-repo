package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out;

import java.util.ArrayList;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SlotsResponseDto {
  ArrayList<DatesDto> dates;

}
