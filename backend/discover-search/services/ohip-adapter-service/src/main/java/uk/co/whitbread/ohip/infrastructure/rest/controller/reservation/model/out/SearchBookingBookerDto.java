package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchBookingBookerDto {

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
