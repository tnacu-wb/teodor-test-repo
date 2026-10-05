package uk.co.whitbread.ohip.domain.model.reservation.out;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.domain.model.reservation.in.StayingGuestAdditionalDetails;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationGuest {

  private String givenName;
  private String surname;
  private String nameTitle;
  private String fullName;
  private String phoneNumber;
  private String email;
  private LocalDate birthDate;
  private String language;
  private GuestAddress address;
  private GuestAddress homeAddress;
  private String id;
  private String type;
  private boolean guestRestricted;
  private StayingGuestAdditionalDetails additionalDetails;
  private Boolean isAccompanyingGuest;
  private String profileId;
}
