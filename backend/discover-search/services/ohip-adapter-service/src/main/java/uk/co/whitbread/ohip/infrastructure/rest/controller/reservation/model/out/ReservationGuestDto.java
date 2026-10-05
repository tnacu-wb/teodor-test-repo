package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class ReservationGuestDto {

  private String givenName;
  private String surname;
  private String nameTitle;
  private String fullName;
  private String phoneNumber;
  private String email;
  private LocalDate birthDate;
  private String language;
  private boolean guestRestricted;
  private String id;
  private String type;
}
