package uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceHotelDto {

  private String hotelId;
  private String name;
  private String addressLine1;
  private String addressLine2;
  private String addressLine3;
  private String addressLine4;
  private String city;
  private String postalCode;
  private String countryCode;
  private String phoneNumber;
  private String businessDate;
  private WbCompanyDto wbCompany;
}
