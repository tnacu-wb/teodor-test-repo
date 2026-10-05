package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CorporateRateDto {

  private String corporateId;
  private List<String> ratePlanSets;
}
