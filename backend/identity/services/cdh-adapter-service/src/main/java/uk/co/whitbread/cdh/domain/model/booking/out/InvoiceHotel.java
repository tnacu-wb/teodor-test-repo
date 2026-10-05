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
public class InvoiceHotel {

  @JsonProperty("HotelId")
  private String hotelId;

  @JsonProperty("Name")
  private String name;

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

  @JsonProperty("CountryCode")
  private String countryCode;

  @JsonProperty("PhoneNumber")
  private String phoneNumber;

  @JsonProperty("BusinessDate")
  private String businessDate;

  @JsonProperty("WhitbreadCompany")
  private WbCompany wbCompany;
}

