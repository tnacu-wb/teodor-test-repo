package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReservationDto {

  @NotEmpty
  @Pattern(regexp = "^[A-Za-z]{6}$", message = "Hotel Id must be exactly 6 alphabetic characters long")
  @Schema(example = "LONSTM", required = true)
  private String hotelId;
  @NotNull
  @Schema(example = "2015-10-20", required = true)
  @FutureOrPresent
  private LocalDate arrival;
  @NotNull
  @Schema(example = "2015-10-21", required = true)
  @FutureOrPresent
  private LocalDate departure;
  @Schema(example = "132484")
  private String basketReferenceId;
  @NotNull
  @Valid
  @Schema(required = true)
  private RoomRateDto roomRates;
  @Min(1)
  @Max(2)
  @Schema(example = "1", required = true)
  private Integer adultsNumber;
  @Schema(example = "0")
  @Min(0)
  @Max(3)
  private Integer childrenNumber;
  @Schema(example = "false")
  private Boolean cotRequired;
  @Schema(example = "bookingNotes")
  private String bookingNotes;
  @Schema(example = "gdsReferenceNumber")
  private String gdsReferenceNumber;
  @Schema(example = "distributionUsername")
  private String distributionUsername;
  @Schema(example = "00000000123456")
  private String distributionIATANumber;
  private List<ReservationPackageDto> reservationPackages;
}
