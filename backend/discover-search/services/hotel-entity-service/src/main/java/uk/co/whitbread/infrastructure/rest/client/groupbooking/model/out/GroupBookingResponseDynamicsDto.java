package uk.co.whitbread.infrastructure.rest.client.groupbooking.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupBookingResponseDynamicsDto {
  private String ticketnumber;
  private String incidentid;
}
