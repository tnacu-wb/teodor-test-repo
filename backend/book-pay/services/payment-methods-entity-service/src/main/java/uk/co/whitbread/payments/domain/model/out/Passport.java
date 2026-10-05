package uk.co.whitbread.payments.domain.model.out;

import lombok.Data;

@Data
public class Passport {
  private String countryOfIssue;
  private String number;
}
