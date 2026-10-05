package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class StayingGuestDetailsDto {

  @Schema(example = "Mrs")
  private String title;
  @NotEmpty
  @Schema(example = "John")
  private String firstName;
  @NotEmpty
  @Schema(example = "Carry")
  private String lastName;
  private String employeeAccountId;
  @Schema(example = "john.carry@email.com")
  private String emailAddress;
  private StayingGuestAddressDto address;
  private StayingGuestAdditionalDetailsDto additionalDetails;
  private String profileId;

}
