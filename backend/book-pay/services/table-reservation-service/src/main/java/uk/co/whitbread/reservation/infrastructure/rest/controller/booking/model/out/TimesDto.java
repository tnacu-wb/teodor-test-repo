package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TimesDto {
  String time;
  boolean available;
  int totalCapacity;
  int remainingCapacity;
  boolean isClosed;
  boolean canEnquire;

}
