package uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuestsDto {

  private boolean leadGuest;
  private String title;
  private String initials;
  private String firstName;
  private String lastName;
  private String bartGuestHistoryNumber;
  private String employeeAccountId;
  private String globalCompanyId;
  private String bartEmployeeId;
  private String emailAddress;
  private String telephoneNumber;
  private String mobileNumber;
  private String companyName;
  private AddressDto address;
  private String nationality;
  private PassportDto passport;
}
