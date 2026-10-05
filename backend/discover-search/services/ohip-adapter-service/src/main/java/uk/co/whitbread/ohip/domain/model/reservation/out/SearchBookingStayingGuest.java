package uk.co.whitbread.ohip.domain.model.reservation.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SearchBookingStayingGuest {

  private String profileId;
  private String title;
  private String firstName;
  private String lastName;

}
