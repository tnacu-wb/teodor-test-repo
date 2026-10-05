package uk.co.whitbread.avail.business.events.infrastructure.model.opera;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessEventDataElement {

  String dataElement;
  String newValue;
  String oldValue;
  String scopeFrom;
  String scopeTo;

}
