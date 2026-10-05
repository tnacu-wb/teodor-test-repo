package uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookerDto {

  private String title;
  private String firstName;
  private String lastName;
  private String emailAddress;
  private String telephoneNumber;
  private String mobileNumber;
  private String companyName;
  private AddressDto address;
}
