package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;


@Data
public class ReservationGuestRequestDto {

  @NotEmpty
  @Schema(example = "LONSTM", required = true)
  @Pattern(regexp = "^[A-Za-z]{6}$", message = "Hotel Id must be exactly 6 alphabetic characters long")
  private String hotelId;
  @NotEmpty
  @Schema(example = "LEI", required = true)
  private String reasonForStay;
  @NotNull
  @Valid
  private BookerDetailsDto booker;
  @NotEmpty
  @Size(min = 1)
  @Valid
  @Schema(required = true)
  private List<StayingGuestDto> stayingGuests;
  @Schema(example = "true")
  private Boolean sendEmailConfirmation;
  @Schema(example = "true")
  private Boolean sendEmailInvoice;
  private String companyAccountId;
  private String userAccountId;
  private String bookingType;
  private String bookerProfileId;
  private String companyProfileId;
  private Boolean preCheckIn = false;
}