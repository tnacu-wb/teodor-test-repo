package uk.co.whitbread.infrastructure.rest.controller.groupbooking.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupBookingResponseDto {

  private String ticketNumber;
  private String incidentId;

}
