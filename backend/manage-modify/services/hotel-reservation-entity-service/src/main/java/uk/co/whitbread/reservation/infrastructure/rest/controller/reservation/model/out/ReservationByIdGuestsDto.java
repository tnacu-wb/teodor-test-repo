package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.StayingGuestAdditionalDetailsDto;

@Data
@AllArgsConstructor
public class ReservationByIdGuestsDto {

  private String givenName;
  private String surName;
  private String nameTitle;
  private String email;
  private String type;
  private GuestAddressDto address;
  private GuestAddressDto homeAddress;
  private StayingGuestAdditionalDetailsDto additionalDetails;
  private Boolean isAccompanyingGuest;
  private String profileId;
  private Boolean sameAsBooker;
}
