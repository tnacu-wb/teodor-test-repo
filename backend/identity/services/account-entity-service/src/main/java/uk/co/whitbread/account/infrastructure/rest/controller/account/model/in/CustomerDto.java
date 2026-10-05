package uk.co.whitbread.account.infrastructure.rest.controller.account.model.in;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerDto {
  private String title;
  private String firstName;
  private String lastName;
  private String nationality;
  private String userId;
  @NotNull
  private String countryOfResidence;
  @NotEmpty
  @Email
  private String customerId;
  @NotNull
  private String language;

  public CustomerDto(String title, String firstName, String lastName, String nationality, String userId,
                     String countryOfResidence, String customerId, String language) {
    this.title = title;
    this.firstName = firstName;
    this.lastName = lastName;
    this.nationality = nationality;
    this.userId = userId;
    this.countryOfResidence = countryOfResidence;
    this.customerId = trimCustomerId(customerId);
    this.language = language;
  }

  public void setCustomerId(String customerId) {
    this.customerId = trimCustomerId(customerId);
  }

  private String trimCustomerId(String customerId) {
    return customerId == null ? null : customerId.trim();
  }
}
