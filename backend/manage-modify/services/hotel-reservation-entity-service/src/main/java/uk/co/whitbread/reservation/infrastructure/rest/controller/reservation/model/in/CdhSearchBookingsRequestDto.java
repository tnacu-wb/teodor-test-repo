package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.shared.commons.validation.CompanyName;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CdhSearchBookingsRequestDto {

  @Parameter(in = ParameterIn.QUERY, name = "bookingReference", example = "LONEUS1391808",
          schema = @Schema(type = "string"))
  private String bookingReference;

  @Parameter(in = ParameterIn.QUERY, name = "bookerLastName", example = "Doe", schema = @Schema(type = "string"))
  private String bookerLastName;

  @Parameter(in = ParameterIn.QUERY, name = "guestLastName", example = "Doe",
          schema = @Schema(type = "string"))
  private String guestLastName;

  @Parameter(in = ParameterIn.QUERY, name = "bookerPostcode", example = "WC2N 5DU",
          schema = @Schema(type = "string"))
  private String bookerPostcode;

  @Parameter(in = ParameterIn.QUERY, name = "hotelId", example = "LONSTM",
          schema = @Schema(type = "string"))
  private String hotelId;

  @Parameter(in = ParameterIn.QUERY, name = "bookerEmail", example = "john.doe@email.com",
          schema = @Schema(type = "string"))
  private String bookerEmail;

  @Parameter(in = ParameterIn.QUERY, name = "bookerPhone", example = "+3905678754",
          schema = @Schema(type = "string"))
  private String bookerPhone;

  @Parameter(in = ParameterIn.QUERY, name = "arrivalDateFrom", example = "2022-03-05",
          schema = @Schema(type = "string"))
  private String arrivalDateFrom;

  @Parameter(in = ParameterIn.QUERY, name = "arrivalDateTo", example = "2022-03-05", schema = @Schema(type = "string"))
  private String arrivalDateTo;

  @Parameter(in = ParameterIn.QUERY, name = "cancellationDate", example = "2022-03-06",
          schema = @Schema(type = "string"))
  private String cancellationDate;

  @Parameter(in = ParameterIn.QUERY, name = "companyName", schema = @Schema(type = "string"))
  @CompanyName
  private String companyName;

  @Parameter(in = ParameterIn.QUERY, name = "thirdPartyBookingReferenceNumber", example = "LONEUS1391808",
          schema = @Schema(type = "string"))
  private String thirdPartyBookingReferenceNumber;

  @Parameter(in = ParameterIn.QUERY, name = "bookingsDatabaseSearch", schema = @Schema(type = "boolean"))
  private boolean bookingsDatabaseSearch;

  @Parameter(in = ParameterIn.QUERY, name = "PageSize", schema = @Schema(type = "integer"))
  private Integer pageSize;

  @Parameter(in = ParameterIn.QUERY, name = "PageNumber", schema = @Schema(type = "integer"))
  private Integer pageNumber;

  @Parameter(in = ParameterIn.QUERY, name = "ContinuationToken", schema = @Schema(type = "string"))
  private String continuationToken;
}
