package uk.co.whitbread.reservation.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.reservation.domain.model.in.StayingGuestAdditionalDetails;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationByIdGuestsResponse {

  private String givenName;
  private String surName;
  private String nameTitle;
  private String email;
  private String type;
  private GuestAddress address;
  private GuestAddress homeAddress;
  private StayingGuestAdditionalDetails additionalDetails;
  private Boolean isAccompanyingGuest;
  private String profileId;
  private Boolean sameAsBooker;
}
