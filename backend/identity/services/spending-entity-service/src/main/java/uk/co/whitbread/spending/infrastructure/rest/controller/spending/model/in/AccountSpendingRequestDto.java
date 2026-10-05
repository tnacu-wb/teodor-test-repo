package uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.whitbread.spending.domain.model.in.Scheme;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.validation.DateFormat;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.validation.ModelValidator;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
public class AccountSpendingRequestDto extends ModelValidator<AccountSpendingRequestDto> {

  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "pibaAccountId",
      example = "PIBA_5f2e7e40-b4fd-42e2-958a-b8b24b61a9d1",
      required = true, schema = @Schema(type = "string"))
  private String pibaAccountId;

  @NotEmpty
  @DateFormat
  @Parameter(in = ParameterIn.QUERY, name = "fromMonthYear", example = "11-2024",
      required = true, schema = @Schema(type = "string"))
  private String fromMonthYear;

  @NotEmpty
  @DateFormat
  @Parameter(in = ParameterIn.QUERY, name = "toMonthYear", example = "09-2025",
      required = true, schema = @Schema(type = "string"))
  private String toMonthYear;

  @Parameter(in = ParameterIn.QUERY, name = "language", example = "EN",
      schema = @Schema(type = "string", allowableValues = {"DE", "EN"}))
  private String language;

  @Parameter(in = ParameterIn.QUERY, name = "scheme", example = "GB",
      schema = @Schema(type = "string", allowableValues = {"DE", "GB"}))
  private Scheme scheme;

  @Parameter(in = ParameterIn.QUERY, name = "tetheredUserGuid",
      example = "5f2e7e40-b4fd-42e2-958a-b8b24b61a9d1",
      schema = @Schema(type = "string"))
  private String tetheredUserGuid;

  public AccountSpendingRequestDto(String pibaAccountId, String fromMonthYear,
      String toMonthYear, String language, Scheme scheme, String tetheredUserGuid) {
    this.pibaAccountId = pibaAccountId;
    this.fromMonthYear = fromMonthYear;
    this.toMonthYear = toMonthYear;
    this.language = language;
    this.scheme = scheme;
    this.tetheredUserGuid = tetheredUserGuid;
  }
}
