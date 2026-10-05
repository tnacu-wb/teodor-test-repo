package uk.co.whitbread.reservation.domain.model.out.slots;

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
public class SessionDates {

  boolean lunchAvailable = false;
  boolean dinnerAvailable = false;
  boolean breakFastAvailable = false;
  String date;
  Session session;
}
