package uk.co.whitbread.avail.business.events.infrastructure.model.opera;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessEventHeader {

  String moduleName;
  String actionType;
  String actionId;
  String primaryKey;
  String publisherId;
  LocalDateTime createDateTime;
  String hotelId;
}
