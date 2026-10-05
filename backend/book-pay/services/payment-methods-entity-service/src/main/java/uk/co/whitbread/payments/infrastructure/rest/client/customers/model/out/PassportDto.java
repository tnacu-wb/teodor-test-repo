package uk.co.whitbread.payments.infrastructure.rest.client.customers.model.out;


import lombok.Data;

@Data
public class PassportDto {
  private String countryOfIssue;
  private String number;
}
