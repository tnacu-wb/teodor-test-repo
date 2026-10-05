package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class StayingGuestDetailsDto {

  @Schema(example = "Mrs")
  private String title;
  @NotEmpty
  @Schema(example = "Debbie")
  private String firstName;
  @NotEmpty
  @Schema(example = "Doe")
  private String lastName;
  private String employeeAccountId;
  @Schema(example = "debbie.doe@whitbread.com")
  private String emailAddress;
  @Valid
  @Schema
  private StayingGuestAddressDto address;
  @Schema
  private StayingGuestAdditionalDetailsDto additionalDetails;
  private String profileId;

}
