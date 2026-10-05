package uk.co.whitbread.hotel.account.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booker {

  private String companyName;

  private String emailAddress;

  private String firstName;

  private String lastName;

  private String mobile;

  private String telephoneNumber;

}
