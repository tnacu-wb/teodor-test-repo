package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchBookingsRequestDto {

  @Parameter(in = ParameterIn.QUERY, name = "bookingReference", example = "LONEUS1391808",
      schema = @Schema(type = "string"))
  private String bookingReference;

  @Parameter(in = ParameterIn.QUERY, name = "bookerLastName", example = "Doe",
      schema = @Schema(type = "string"))
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

  @Parameter(in = ParameterIn.QUERY, name = "arrivalDate", example = "2022-03-05", schema = @Schema(type = "string"))
  private String arrivalDate;

  @Parameter(in = ParameterIn.QUERY, name = "cancellationDate", example = "2022-03-06",
      schema = @Schema(type = "string"))
  private String cancellationDate;

  @Parameter(in = ParameterIn.QUERY, name = "companyName", schema = @Schema(type = "string"))
  private String companyName;

  @Parameter(in = ParameterIn.QUERY, name = "thirdPartyBookingReferenceNumber", example = "LONEUS1391808",
      schema = @Schema(type = "string"))
  private String thirdPartyBookingReferenceNumber;

  @Parameter(in = ParameterIn.QUERY, name = "offset", example = "0",
      schema = @Schema(type = "int", defaultValue = "0"))
  private int offset = 0;
  @Parameter(in = ParameterIn.QUERY, name = "limit", example = "20",
      schema = @Schema(type = "int", defaultValue = "20"))
  private int limit = 20;

}
