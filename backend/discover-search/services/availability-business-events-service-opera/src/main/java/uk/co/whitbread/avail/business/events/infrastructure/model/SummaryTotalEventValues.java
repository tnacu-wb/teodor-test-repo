package uk.co.whitbread.avail.business.events.infrastructure.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SummaryTotalEventValues {

  private String roomType;
  private int quantity;
  private String date;

}
