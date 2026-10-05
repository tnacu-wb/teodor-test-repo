package uk.co.whitbread.reservation.domain.model.out.slots;

import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Value
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class SessionResponse {
  List<SessionDates> dates;

  public SessionResponse(List<SessionDates> dates) {
    this.dates = dates;
  }
}
