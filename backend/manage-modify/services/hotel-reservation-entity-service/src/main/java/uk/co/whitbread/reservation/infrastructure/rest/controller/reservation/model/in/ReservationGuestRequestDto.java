package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReservationGuestRequestDto {

  @NotEmpty
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String basketReference;
  @NotEmpty
  @Schema(example = "LONSTM", requiredMode = Schema.RequiredMode.REQUIRED)
  @Pattern(regexp = "^[A-Za-z]{6}$", message = "Hotel Id must be exactly 6 alphabetic characters long")
  private String hotelId;
  @NotEmpty
  @Schema(example = "LEI", requiredMode = Schema.RequiredMode.REQUIRED)
  private String reasonForStay;
  @Valid
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private BookerDetailsDto booker;
  @NotEmpty
  @Size(min = 1)
  @Valid
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private List<StayingGuestDto> stayingGuests;
  @Schema(example = "false")
  private Boolean sendEmailConfirmation;
  @Schema(example = "false")
  private Boolean sendEmailInvoice;
  private String bookerProfileId;
  private String companyProfileId;
  private Boolean preCheckIn = false;
  private String companyId;
  private Boolean updateProfileConsent;
}
