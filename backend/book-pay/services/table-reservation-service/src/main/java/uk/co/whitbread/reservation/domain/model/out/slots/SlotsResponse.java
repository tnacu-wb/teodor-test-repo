package uk.co.whitbread.reservation.domain.model.out.slots;

import java.util.ArrayList;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SlotsResponse {
  ArrayList<Dates> dates;

}
