package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.StayingGuestAdditionalDetailsDto;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReservationByIdGuestsDto {

  private String givenName;
  private String surName;
  private String nameTitle;
  private String email;
  private String type;
  private ReservationByIdGuestAddressDto address;
  private ReservationByIdGuestAddressDto homeAddress;
  private StayingGuestAdditionalDetailsDto additionalDetails;
  private Boolean isAccompanyingGuest;
  private String profileId;
  private Boolean sameAsBooker;
}
