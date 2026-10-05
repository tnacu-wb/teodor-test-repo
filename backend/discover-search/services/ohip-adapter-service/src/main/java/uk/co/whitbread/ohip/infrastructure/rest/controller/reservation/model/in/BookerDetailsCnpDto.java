package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookerDetailsCnpDto {

  private String title;
  private String firstName;
  private String lastName;
  private String mobile;
  private String landline;
  private String emailAddress;
  private String companyName;
  private BookerAddressCnpDto address;
}
