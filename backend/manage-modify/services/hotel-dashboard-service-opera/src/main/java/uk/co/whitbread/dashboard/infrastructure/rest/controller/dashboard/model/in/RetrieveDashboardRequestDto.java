package uk.co.whitbread.dashboard.infrastructure.rest.controller.dashboard.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import uk.co.whitbread.dashboard.infrastructure.rest.controller.dashboard.validation.NotNullIfAnotherFieldHasValue;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@NotNullIfAnotherFieldHasValue(values = {"confirmationNumber", "surname", "arrivalDate"})
public class RetrieveDashboardRequestDto {

  private String confirmationNumber;

  private String surname;

  @Schema(type = "string", format = "date", example = "2022-01-01")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate arrivalDate;

  private boolean business;

  private String companyId;

  private String employeeId;

  private String language;
}
