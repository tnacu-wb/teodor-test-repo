package uk.co.whitbread.cdh.infrastructure.rest.controller.spending.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.cdh.infrastructure.rest.controller.validation.MonthYearFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSpendRequestDto {

  @MonthYearFormat
  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "fromMonthYear", example = "01-2024",
      required = true, schema = @Schema(type = "string"),
      description = "Start month-year in MM-YYYY format")
  private String fromMonthYear;

  @MonthYearFormat
  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "toMonthYear", example = "03-2026",
      required = true, schema = @Schema(type = "string"),
      description = "End month-year in MM-YYYY format")
  private String toMonthYear;

  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "accessContext", required = true,
      schema = @Schema(type = "string"),
      description = "Access context for audit purposes")
  private String accessContext;

  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "accessedBy", required = true,
      schema = @Schema(type = "string"),
      description = "User who accessed the data for audit purposes")
  private String accessedBy;
}
