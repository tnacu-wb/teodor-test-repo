package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ReservationProfilesDto {

  private String bookerProfileId;
  private String companyProfileId;
}
