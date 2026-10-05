package uk.co.whitbread.domain.model.groupbooking.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupBookingResponse {

  private String ticketNumber;
  private String incidentId;

}
