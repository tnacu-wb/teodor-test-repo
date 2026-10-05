package uk.co.whitbread.hotel.register.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import uk.co.whitbread.hotel.register.validation.Phone;

@Data
public class InnBRegistrationStepTwoRequest {

  @NotBlank
  private String title;

  @NotBlank
  private String firstName;

  @NotBlank
  private String lastName;

  @Phone
  private String phoneNumber;

  @NotBlank
  private String password;

  @NotBlank
  private String activationKey;

  private UpdatePreferencesRequest updatePreferencesRequest;

  private String companySector;
  private String averageMonthlyBooking;
  private String numberOfEmployee;

}
