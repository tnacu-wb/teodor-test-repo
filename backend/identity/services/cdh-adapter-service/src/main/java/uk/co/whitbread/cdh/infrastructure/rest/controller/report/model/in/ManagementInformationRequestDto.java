package uk.co.whitbread.cdh.infrastructure.rest.controller.report.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.cdh.infrastructure.rest.controller.validation.DateFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManagementInformationRequestDto {

  @DateFormat
  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "fromDate", example = "2023-10-05",
      required = true, schema = @Schema(type = "string"))
  private String fromDate;

  @DateFormat
  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "toDate", example = "2023-10-05",
      required = true, schema = @Schema(type = "string"))
  private String toDate;

  @NotEmpty
  private String accessContext;
  @NotEmpty
  private String accessedBy;
}
