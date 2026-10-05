package uk.co.whitbread.company.infrastructure.rest.controller.company.model.in;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompaniesProfileRequestDto {

  @NotEmpty
  private String hotelId;
  private String companyName;
  private String arNumber;
  @Min(1)
  private int limit;
}
