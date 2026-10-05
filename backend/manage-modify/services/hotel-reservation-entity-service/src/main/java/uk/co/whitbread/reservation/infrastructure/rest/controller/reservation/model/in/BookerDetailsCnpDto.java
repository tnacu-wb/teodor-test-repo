package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.shared.commons.validation.CompanyName;

@Data
@Builder
public class BookerDetailsCnpDto {

  @Schema(example = "Mr")
  private String title;
  @Schema(example = "Test")
  private String firstName;
  @Schema(example = "Testerson")
  private String lastName;
  @Schema(example = "+44123123")
  private String mobile;
  @Schema(example = "+44123123")
  private String landline;
  @Schema(example = "test@mailinator.com")
  private String emailAddress;
  @Schema(example = "ABCD1234")
  @CompanyName
  private String companyName;
  @Schema
  private BookerAddressCnpDto address;
}
