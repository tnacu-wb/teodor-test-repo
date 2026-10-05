package uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.validation.DateFormat;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.validation.ModelValidator;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
public class CompanySpendingRequestDto extends ModelValidator<CompanySpendingRequestDto> {

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

  public CompanySpendingRequestDto(String fromMonthYear, String toMonthYear) {
    this.fromMonthYear = fromMonthYear;
    this.toMonthYear = toMonthYear;
  }
}
