package uk.co.whitbread.reservation.domain.model.out.slots;

import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Dates {

  String date;
  List<Times> times;

}
