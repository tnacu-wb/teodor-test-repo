package uk.co.whitbread.ohip.domain.model.reservation.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SearchBookingBooker {

  private String profileId;
  private String title;
  private String firstName;
  private String lastName;
  private String email;
  private String mobile;
  private String landline;
  private String postcode;
  private String company;
}
