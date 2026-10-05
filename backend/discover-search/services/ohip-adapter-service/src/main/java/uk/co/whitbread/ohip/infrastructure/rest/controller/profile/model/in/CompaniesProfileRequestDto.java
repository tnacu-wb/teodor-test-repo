package uk.co.whitbread.ohip.infrastructure.rest.controller.profile.model.in;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompaniesProfileRequestDto {
  @NotEmpty
  private String hotelId;
  private String arNumber;
  private String companyName;
  @Min(1)
  private int limit;
}
