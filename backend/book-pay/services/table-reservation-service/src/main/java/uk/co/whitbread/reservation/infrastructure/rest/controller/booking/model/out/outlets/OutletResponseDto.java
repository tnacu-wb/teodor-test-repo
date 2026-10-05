package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.outlets;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class OutletResponseDto {

  private List<CompanyDto> companies;

  public OutletResponseDto(List<CompanyDto> companies) {
    this.companies = companies;
  }


}
