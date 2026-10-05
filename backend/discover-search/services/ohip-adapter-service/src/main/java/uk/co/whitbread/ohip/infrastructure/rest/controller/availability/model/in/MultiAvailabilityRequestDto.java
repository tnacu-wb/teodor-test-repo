package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.infrastructure.rest.controller.validation.DateFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultiAvailabilityRequestDto {

  @NotNull(message = "at least one hotelId must be specified")

  @ArraySchema(arraySchema = @Schema(required = true,
      name = "hotelIds"))
  private String[] hotelIds;

  @DateFormat
  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "arrivalDate", example = "2022-03-01",
      required = true, schema = @Schema(type = "string"))
  private String arrivalDate;

  @DateFormat
  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "departureDate", example = "2022-03-05",
      required = true, schema = @Schema(type = "string"))
  private String departureDate;

  @NotNull(message = "at least one numberOfRooms must be specified")

  @ArraySchema(arraySchema = @Schema(required = true,
      name = "numberOfRooms",
      example = "[\"1\", \"2\"]"))
  private Integer[] numberOfRooms;

  @ArraySchema(arraySchema = @Schema(required = true,
      name = "roomTypes",
      example = "[\"DB\", \"TWIN\"]"))
  private String[] roomTypes;

  @NotNull(message = "at least one roomType must be specified")

  @ArraySchema(arraySchema = @Schema(required = true,
      name = "adults",
      example = "[\"2\", \"0\"]"))
  private Integer[] adults;

  @ArraySchema(arraySchema = @Schema(
      name = "children",
      example = "[\"2\", \"0\"]"))
  private Integer[] children;

  @ArraySchema(arraySchema = @Schema(
      name = "cotsRequired",
      example = "[\"true\", \"false\"]"))
  private Boolean[] cotsRequired;

  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "channel", example = "PI",
      required = true, schema = @Schema(type = "string"))
  private String channel;

  @Parameter(in = ParameterIn.QUERY,
      name = "companyId",
      example = "2569623", schema = @Schema(type = "string"))
  private String companyId;
}
