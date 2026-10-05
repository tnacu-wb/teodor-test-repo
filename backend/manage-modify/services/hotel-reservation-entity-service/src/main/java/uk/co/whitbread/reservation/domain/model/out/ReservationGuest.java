package uk.co.whitbread.reservation.domain.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ReservationGuest {

  private String givenName;
  private String surname;
  @JsonProperty("nameTitle")
  private String title;
  private String fullName;
  private String phoneNumber;
  private String email;
  private LocalDate birthDate;
  private String language;
  private boolean guestRestricted;
  private String id;
  private String type;
}
