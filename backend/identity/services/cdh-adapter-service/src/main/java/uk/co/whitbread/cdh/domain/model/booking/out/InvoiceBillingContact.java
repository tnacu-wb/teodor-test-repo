package uk.co.whitbread.cdh.domain.model.booking.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceBillingContact {

  @JsonProperty("Title")
  private String title;

  @JsonProperty("FirstName")
  private String firstName;

  @JsonProperty("LastName")
  private String lastName;

  @JsonProperty("CompanyName")
  private String companyName;

  @JsonProperty("BlockCode")
  private String blockCode;

  @JsonProperty("GroupName")
  private String groupName;

  @JsonProperty("AddressLine1")
  private String addressLine1;

  @JsonProperty("AddressLine2")
  private String addressLine2;

  @JsonProperty("AddressLine3")
  private String addressLine3;

  @JsonProperty("AddressLine4")
  private String addressLine4;

  @JsonProperty("City")
  private String city;

  @JsonProperty("PostalCode")
  private String postalCode;

  @JsonProperty("Country")
  private String country;

  @JsonProperty("PhoneNumber")
  private String phoneNumber;

  @JsonProperty("EmailAddress")
  private String emailAddress;
}

