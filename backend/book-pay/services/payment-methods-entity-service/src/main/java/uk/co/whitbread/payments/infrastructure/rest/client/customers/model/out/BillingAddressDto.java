package uk.co.whitbread.payments.infrastructure.rest.client.customers.model.out;


import lombok.Data;

@Data
public class BillingAddressDto {
  private String companyName;
  private String countryCode;
  private String line1;
  private String line2;
  private String line3;
  private String line4;
  private String line5;
  private String postCode;
  private String type;
}
