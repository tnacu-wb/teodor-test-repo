package uk.co.whitbread.avail.business.events.infrastructure.model.opera;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessEventDetails {

  Set<EventDetail> businessEventDataElements;
}
