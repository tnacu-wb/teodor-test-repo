package uk.co.whitbread.account.domain.model.in;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Passport {
  private String number;
  private String countryOfIssue;
}
