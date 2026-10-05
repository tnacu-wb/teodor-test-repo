package uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceBillingContactDto {

  private String title;
  private String firstName;
  private String lastName;
  private String companyName;
  private String blockCode;
  private String groupName;
  private String addressLine1;
  private String addressLine2;
  private String addressLine3;
  private String addressLine4;
  private String city;
  private String postalCode;
  private String country;
  private String phoneNumber;
  private String emailAddress;
}
