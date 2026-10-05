package uk.co.whitbread.reservation.domain.model.out.slots;

import java.util.List;
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
public class Session {
  Times times;
  List<Times> breakFast;
  List<Times> lunch;
  List<Times> dinner;


}
