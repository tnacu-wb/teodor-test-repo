package uk.co.whitbread.account.domain.model.in;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class Customer {
  private String title;
  private String firstName;
  private String lastName;
  private String nationality;
  private String userId;
  @NotNull
  private String countryOfResidence;
  private String customerId;
  @NotNull
  private String language;
}
