package uk.co.whitbread.payments.domain.model.out;

import lombok.Data;

@Data
public class AdditionalGuest {

  private Address address;
  private String carRegistration;
  private Long dialingCode;
  private String email;
  private String firstName;
  private String lastName;
  private String mobile;
  private String nationality;
  private Passport passport;
  private String telephone;
  private String title;
}
