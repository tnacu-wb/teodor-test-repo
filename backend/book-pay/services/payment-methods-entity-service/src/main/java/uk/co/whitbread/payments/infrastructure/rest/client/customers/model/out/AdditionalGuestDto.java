package uk.co.whitbread.payments.infrastructure.rest.client.customers.model.out;


import lombok.Data;

@Data
public class AdditionalGuestDto {
  private AddressDto address;
  private String carRegistration;
  private Long dialingCode;
  private String email;
  private String firstName;
  private String lastName;
  private String mobile;
  private String nationality;
  private PassportDto passport;
  private String telephone;
  private String title;
}
