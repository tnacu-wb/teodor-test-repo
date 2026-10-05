package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.Data;

@Data
public class ReservationDto {

  @Schema(example = "LONSTM")
  @Pattern(regexp = "^[A-Za-z]{6}$", message = "Hotel Id must be exactly 6 alphabetic characters long")
  private String hotelId;
  @NotNull
  @Schema(example = "2015-10-20", required = true)
  private String arrival;
  @NotNull
  @Schema(example = "2015-10-21", required = true)
  private String departure;
  @Schema(example = "132484")
  private String externalReferenceId;
  @NotNull
  @Valid
  @Schema(required = true)
  private RoomRateDto roomRates;
  @Min(1)
  @Max(2)
  @Schema(example = "1", required = true)
  private Integer adults;
  @Min(0)
  @Max(3)
  @Schema(example = "0")
  private Integer children;
  @Schema(example = "false")
  private Boolean cotRequired;
  @Schema(example = "ABCD1234")
  private String gdsReferenceNumber;
  @Schema(example = "nonSmoking")
  private String bookingNotes;
  @Schema(example = "testUsername")
  private String distributionUsername;
  @Schema(example = "00000000123456")
  private String distributionIATANumber;
  @Schema(example = "EMPL_5404e3cf-0c57-4987-84f6-3f70a4079bdc")
  private String userAccountId;
  @Schema(example = "COMP_7846063e-acd1-4931-8c07-1c33ddc2a42f")
  private String companyAccountId;
  @Schema(example = "ANON")
  private String bookingType;
  private LeadGuestDto leadGuest;
  private String ccuiUserEmailId;
  @Schema(example = "12345")
  private String operaCompanyId;
  private List<ReservationPackagesDto> reservationPackages;
}
