package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class BookerDetailsDto {

  @Schema(example = "Mrs")
  private String title;
  @NotEmpty
  @Schema(example = "John")
  private String firstName;
  @NotEmpty
  @Schema(example = "Carry")
  private String lastName;
  @Schema(example = "john.carry@email.com")
  private String emailAddress;
  @Schema(example = "true")
  private Boolean acceptFutureMailing;
  @Schema(example = "+3905678754")
  private String mobile;
  @Schema(example = "+3905678754")
  private String landline;
  @Schema(example = "en")
  private String language;
  @Valid
  @Schema
  private BookerAddressDto address;

}
