package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationProfilesDto {

  private String bookerProfileId;
  private String companyProfileId;
}
