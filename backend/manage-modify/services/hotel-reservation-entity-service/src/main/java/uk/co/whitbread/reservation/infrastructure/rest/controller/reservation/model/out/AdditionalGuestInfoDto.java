package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdditionalGuestInfoDto {

  private String purposeOfStay;
  private Boolean acceptFutureMailing;

}
